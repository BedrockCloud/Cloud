<?php

namespace bedrockcloud;

use Phar;
use bedrockcloud\command\CommandManager;
use bedrockcloud\config\impl\DefaultConfig;
use bedrockcloud\config\impl\MaintenanceList;
use bedrockcloud\config\impl\ModuleConfig;
use bedrockcloud\config\impl\NotifyList;
use bedrockcloud\console\Console;
use bedrockcloud\console\log\CloudLogSaver;
use bedrockcloud\event\EventManager;
use bedrockcloud\event\impl\cloud\CloudStartedEvent;
use bedrockcloud\http\HttpServer;
use bedrockcloud\language\Language;
use bedrockcloud\library\LibraryManager;
use bedrockcloud\network\Network;
use bedrockcloud\plugin\CloudPluginManager;
use bedrockcloud\scheduler\AsyncPool;
use bedrockcloud\server\CloudServerManager;
use bedrockcloud\setup\impl\ConfigSetup;
use bedrockcloud\setup\impl\TemplateSetup;
use bedrockcloud\setup\Setup;
use bedrockcloud\software\SoftwareManager;
use bedrockcloud\template\Template;
use bedrockcloud\template\TemplateManager;
use bedrockcloud\thread\ThreadManager;
use bedrockcloud\update\UpdateChecker;
use bedrockcloud\util\Address;
use bedrockcloud\util\ClassLoader;
use bedrockcloud\util\CloudLogger;
use bedrockcloud\util\ExceptionHandler;
use bedrockcloud\util\ReloadableList;
use bedrockcloud\util\ShutdownHandler;
use bedrockcloud\util\TickableList;
use bedrockcloud\util\Utils;
use bedrockcloud\util\VersionInfo;
use bedrockcloud\web\WebAccountManager;
use pocketmine\snooze\SleeperHandler;

final class BedrockCloud {

    private static self $instance;
    private bool $running = true;
    private bool $reloading = false;
    private int $tick = 0;
    private SleeperHandler $sleeperHandler;
    private Console $console;
    private Network $network;
    private HttpServer $httpServer;

    public function __construct(private readonly ClassLoader $classLoader) {
        self::$instance = $this;

        ExceptionHandler::set();
        ShutdownHandler::register();

        Utils::createDefaultFiles();
        LibraryManager::getInstance()->load();

        Utils::check();
        $this->sleeperHandler = new SleeperHandler();
        $this->console = new Console();
        $this->console->start();

        Utils::downloadFiles();
        SoftwareManager::getInstance()->downloadAll();
        Utils::clearConsole();
        if (FIRST_RUN) {
            (new ConfigSetup())->completion(function(array $results): void {
                $this->start();
                if ($results["defaultLobbyTemplate"] ?? true) {
                    TemplateManager::getInstance()->createTemplate(Template::lobby("Lobby"));
                }

                if ($results["defaultProxyTemplate"] ?? true) {
                    TemplateManager::getInstance()->createTemplate(Template::proxy("Proxy"));
                }
            })->startSetup();
        } else $this->start();
        $this->tick();
    }

    public function start(): void {
        ini_set("memory_limit", ($memory = DefaultConfig::getInstance()->getMemoryLimit()) > 0 ? $memory . "M" : "-1");

        CloudLogger::get()->info("§bBesrock§3Cloud §8(§ev" . VersionInfo::VERSION . (VersionInfo::BETA ? "@BETA" : "") . "§8) - §rdeveloped by §e" . implode("§8, §e", VersionInfo::DEVELOPERS));
        CloudLogger::get()->emptyLine();

        CloudLogger::get()->info(Language::current()->translate("cloud.starting"));
        $startTime = microtime(true);
        Utils::createLockFile();

        $this->network = new Network(new Address("127.0.0.1", DefaultConfig::getInstance()->getNetworkPort()));
        $this->httpServer = new HttpServer(new Address("0.0.0.0", DefaultConfig::getInstance()->getHttpServerPort()));

        TemplateManager::getInstance()->loadTemplates();
        CloudPluginManager::getInstance()->loadPlugins();
        CloudPluginManager::getInstance()->enablePlugins();

        TickableList::add(CloudPluginManager::getInstance());
        TickableList::add(AsyncPool::getInstance());
        TickableList::add(CloudServerManager::getInstance());
        TickableList::add(TemplateManager::getInstance());
        ReloadableList::add(CommandManager::getInstance());
        ReloadableList::add(CloudPluginManager::getInstance());
        ReloadableList::add(TemplateManager::getInstance());
        ReloadableList::add(new MaintenanceList());
        ReloadableList::add(new NotifyList());
        ReloadableList::add(DefaultConfig::getInstance());
        ReloadableList::add(ModuleConfig::getInstance());
        ReloadableList::add($this->httpServer);

        $this->network->init();

        if (DefaultConfig::getInstance()->isHttpServerEnabled()) $this->httpServer->init();
        if (DefaultConfig::getInstance()->isWebEnabled()) {
            ReloadableList::add(WebAccountManager::getInstance());
            WebAccountManager::getInstance()->loadAccounts();
        }

        if (DefaultConfig::getInstance()->isUpdateChecks()) {
            UpdateChecker::getInstance()->check();
        } else {
            if (Language::current() === Language::ENGLISH()) CloudLogger::get()->info("The cloud does not check for updates. Please watch the github or discord more closely.");
            else CloudLogger::get()->info("Die Cloud überprüft nun nicht mehr, ob Updates verfügbar sind. Bitte behalte den Discord oder GitHub besser im Blick.");
        }

        $startedTime = (microtime(true) - $startTime);
        (new CloudStartedEvent($startedTime))->call();
        CloudLogger::get()->info(Language::current()->translate("cloud.started", number_format($startedTime, 3)));
        if (count(TemplateManager::getInstance()->getTemplates()) == 0 && FIRST_RUN) {
            CloudLogger::get()->info(Language::current()->translate("cloud.no.templates"));
            (new TemplateSetup())->startSetup();
        }

        $this->network->start();
    }

    public function reload(): void {
        if (!$this->reloading) {
            $this->reloading = true;
            $startTime = microtime(true);
            CloudLogger::get()->info(Language::current()->translate("reload.start"));
            $currentLanguage = Language::current()->getName();
            ReloadableList::reload();
            $startedTime = (microtime(true) - $startTime);
            $newLanguage = Language::current()->getName();
            if ($currentLanguage !== $newLanguage) {
                DefaultConfig::getInstance()->setLanguage($currentLanguage);
                CloudLogger::get()->warn(Language::current()->translate("reload.change.language", $newLanguage));
            }

            CloudLogger::get()->info(Language::current()->translate("reload.success", number_format($startedTime, 3)));

            $this->reloading = false;
        }
    }

    public function tick(): void {
        $start = microtime(true);
        while ($this->running) {
            $this->sleeperHandler->sleepUntil($start);
            usleep(50 * 1000);
            $this->tick++;
            TickableList::tick($this->tick);
        }
    }

    public function shutdown(): void {
        if ($this->running) {
            $this->running = false;
            Setup::getCurrentSetup()?->cancel();
            ShutdownHandler::unregister();
            EventManager::getInstance()->removeAll();
            CloudServerManager::getInstance()->stopAll(true);
            CloudPluginManager::getInstance()->disablePlugins();
            AsyncPool::getInstance()->shutdown();
            if (isset($this->network)) $this->network->close();
            if (isset($this->network)) $this->console->quit();
            if (isset($this->network)) $this->httpServer->close();
            ThreadManager::getInstance()->stopAll();
            Utils::deleteLockFile();
            CloudLogger::get()->info(Language::current()->translate("cloud.stopped"));
            CloudLogger::close();
            CloudLogSaver::clear();
            Utils::kill(getmypid());
        }
    }

    public function getClassLoader(): ClassLoader {
        return $this->classLoader;
    }

    public function getHttpServer(): HttpServer {
        return $this->httpServer;
    }

    public function getNetwork(): Network {
        return $this->network;
    }

    public function getConsole(): Console {
        return $this->console;
    }

    public function getSleeperHandler(): SleeperHandler {
        return $this->sleeperHandler;
    }

    public function getTick(): int {
        return $this->tick;
    }

    public function isReloading(): bool {
        return $this->reloading;
    }

    public function isRunning(): bool {
        return $this->running;
    }

    public static function getInstance(): BedrockCloud {
        return self::$instance;
    }
}

require_once "util/ClassLoader.php";
require_once "BedrockCloud.php";

define("IS_PHAR", Phar::running() !== "");
define("SOURCE_PATH", __DIR__ . "/");


if (IS_PHAR) {
    define("CLOUD_PATH", str_replace("phar://", "", dirname(__DIR__, 3) . DIRECTORY_SEPARATOR));
} else {
    define("CLOUD_PATH", dirname(__DIR__, 2) . DIRECTORY_SEPARATOR);
}

define("STORAGE_PATH", CLOUD_PATH . "storage/");
define("CRASH_PATH", CLOUD_PATH . "storage/crashes/");
define("LIBRARY_PATH", STORAGE_PATH . "libraries/");
define("PLUGINS_PATH", STORAGE_PATH . "plugins/");
define("SERVER_PLUGINS_PATH", STORAGE_PATH . "plugins/server/");
define("PROXY_PLUGINS_PATH", STORAGE_PATH . "plugins/proxy/");
define("CLOUD_PLUGINS_PATH", STORAGE_PATH . "plugins/cloud/");
define("SOFTWARE_PATH", STORAGE_PATH . "software/");
define("IN_GAME_PATH", STORAGE_PATH . "inGame/");
define("WEB_PATH", STORAGE_PATH . "web/");
define("LOG_PATH", STORAGE_PATH . "cloud.log");
define("TEMP_PATH", CLOUD_PATH . "tmp/");
define("TEMPLATES_PATH", CLOUD_PATH . "templates/");
define("FIRST_RUN", !file_exists(STORAGE_PATH . "config.json"));

$classLoader = new ClassLoader();
$classLoader->init();

new BedrockCloud($classLoader);