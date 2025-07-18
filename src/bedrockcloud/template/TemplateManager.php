<?php

namespace bedrockcloud\template;

use bedrockcloud\config\impl\MaintenanceList;
use bedrockcloud\config\type\ConfigTypes;
use bedrockcloud\event\impl\template\TemplateCreateEvent;
use bedrockcloud\event\impl\template\TemplateDeleteEvent;
use bedrockcloud\event\impl\template\TemplateEditEvent;
use bedrockcloud\language\Language;
use bedrockcloud\network\Network;
use bedrockcloud\network\packet\impl\normal\TemplateSyncPacket;
use bedrockcloud\player\CloudPlayer;
use bedrockcloud\player\CloudPlayerManager;
use bedrockcloud\BedrockCloud;
use bedrockcloud\server\CloudServer;
use bedrockcloud\server\CloudServerManager;
use bedrockcloud\server\status\ServerStatus;
use bedrockcloud\server\utils\PropertiesMaker;
use bedrockcloud\util\CloudLogger;
use bedrockcloud\config\Config;
use bedrockcloud\util\Reloadable;
use bedrockcloud\util\SingletonTrait;
use bedrockcloud\util\Tickable;
use bedrockcloud\util\Utils;

final class TemplateManager implements Reloadable, Tickable {
    use SingletonTrait;

    /** @var array<Template> */
    private array $templates = [];
    private Config $templatesConfig;

    public function __construct() {
        self::setInstance($this);
        $this->templatesConfig = new Config(TEMPLATES_PATH . "templates.json", ConfigTypes::JSON());
    }

    public function loadTemplates(): void {
        CloudLogger::get()->info(Language::current()->translate("template.loading"));
        foreach ($this->templatesConfig->getAll() as $name => $data) {
            CloudLogger::get()->debug("Loading template " . ($data["name"] ?? $name));
            if (($template = Template::fromArray($data)) instanceof Template) {
                $this->templates[$template->getName()] = $template;
            }
        }

        if (count($this->templates) == 0) {
            CloudLogger::get()->info(Language::current()->translate("template.loaded.none"));
        } else {
            CloudLogger::get()->info(Language::current()->translate("template.loaded", count($this->templates)));
        }
    }

    public function createTemplate(Template $template): void {
        $startTime = microtime(true);
        CloudLogger::get()->info(Language::current()->translate("template.create", $template->getName()));
        $this->templatesConfig->set($template->getName(), $template->toArray());
        $this->templatesConfig->save();

        (new TemplateCreateEvent($template))->call();

        CloudLogger::get()->debug("Creating directory: " . $template->getPath());
        if (!file_exists($template->getPath())) mkdir($template->getPath());
        PropertiesMaker::makeProperties($template);
        $this->templates[$template->getName()] = $template;
        CloudLogger::get()->info(Language::current()->translate("template.created", $template->getName(), number_format((microtime(true) - $startTime), 3)));
        Network::getInstance()->broadcastPacket(new TemplateSyncPacket($template));
    }

    public function deleteTemplate(Template $template): void {
        $startTime = microtime(true);
        CloudLogger::get()->info(Language::current()->translate("template.delete", $template->getName()));
        $this->templatesConfig->remove($template->getName());
        $this->templatesConfig->save();

        (new TemplateDeleteEvent($template))->call();

        CloudServerManager::getInstance()->stopTemplate($template);

        if (file_exists($template->getPath())) Utils::deleteDir($template->getPath());
        if (isset($this->templates[$template->getName()])) unset($this->templates[$template->getName()]);
        CloudLogger::get()->info(Language::current()->translate("template.deleted", $template->getName(), number_format((microtime(true) - $startTime), 3)));
        Network::getInstance()->broadcastPacket(new TemplateSyncPacket($template, true));
    }

    public function editTemplate(Template $template, ?bool $lobby, ?bool $maintenance, ?bool $static, ?int $maxPlayerCount, ?int $minServerCount, ?int $maxServerCount, ?float $startNewPercentage, ?bool $autoStart): void {
        $startTime = microtime(true);
        CloudLogger::get()->info(Language::current()->translate("template.edit", $template->getName()));
        $template->getSettings()->setLobby(($lobby === null ? $template->getSettings()->isLobby() : $lobby));
        $template->getSettings()->setMaintenance(($maintenance === null ? $template->getSettings()->isMaintenance() : $maintenance));
        $template->getSettings()->setStatic(($static === null ? $template->getSettings()->isStatic() : $static));
        $template->getSettings()->setMaxPlayerCount(($maxPlayerCount === null ? $template->getSettings()->getMaxPlayerCount() : $maxPlayerCount));
        $template->getSettings()->setMinServerCount(($minServerCount === null ? $template->getSettings()->getMinServerCount() : $minServerCount));
        $template->getSettings()->setMaxServerCount(($maxServerCount === null ? $template->getSettings()->getMaxServerCount() : $maxServerCount));
        $template->getSettings()->setStartNewPercentage(($startNewPercentage === null ? $template->getSettings()->getStartNewPercentage() : $startNewPercentage));
        $template->getSettings()->setAutoStart(($autoStart === null ? $template->getSettings()->isAutoStart() : $autoStart));

        (new TemplateEditEvent($template, $lobby, $maintenance, $static, $maxPlayerCount, $minServerCount, $maxServerCount, $startNewPercentage, $autoStart))->call();

        $this->templatesConfig->set($template->getName(), $template->toArray());
        $this->templatesConfig->save();
        CloudLogger::get()->info(Language::current()->translate("template.edited", $template->getName(), number_format((microtime(true) - $startTime), 3)));
        Network::getInstance()->broadcastPacket(new TemplateSyncPacket($template));

        if ($template->toArray()["maintenance"]) {
            foreach (array_filter(CloudPlayerManager::getInstance()->getPlayers(), function(CloudPlayer $player) use($template): bool {
                return ($player->getCurrentServer() !== null && $player->getCurrentServer()->getTemplate() === $template) && !MaintenanceList::is($player->getName());
            }) as $player) {
                $player->kick("MAINTENANCE");
            }
        }
    }

    public function reload(): bool {
        $this->templatesConfig->reload();
        foreach ($this->templatesConfig->getAll() as $name => $templateData) {
            if (isset($this->templates[$templateData["name"] ?? $name])) {
                ($template = $this->templates[$templateData["name"]])->getSettings()->setLobby($templateData["lobby"]);
                $template->getSettings()->setMaintenance($templateData["maintenance"]);
                $template->getSettings()->setStatic($templateData["static"]);
                $template->getSettings()->setMaxPlayerCount($templateData["maxPlayerCount"]);
                $template->getSettings()->setMinServerCount($templateData["minServerCount"]);
                $template->getSettings()->setMaxServerCount($templateData["maxServerCount"]);
                $template->getSettings()->setStartNewPercentage($templateData["startNewPercentage"]);
                $template->getSettings()->setAutoStart($templateData["autoStart"]);
            } else {
                if (($template = Template::fromArray($templateData)) !== null) {
                    $this->createTemplate($template);
                }
            }
        }

        foreach ($this->templates as $template) {
            if (!$this->templatesConfig->has($template->getName())) $this->deleteTemplate($template);
        }
        return true;
    }

    public function checkTemplate(string $name): bool {
        return $this->templatesConfig->has($name);
    }

    public function tick(int $currentTick): void {
        if (BedrockCloud::getInstance()->isReloading()) return;
        foreach (TemplateManager::getInstance()->getTemplates() as $template) {
            if ($template->getSettings()->isAutoStart()) {
                if (($running = count(CloudServerManager::getInstance()->getServersByTemplate($template))) < $template->getSettings()->getMaxServerCount()) {
                    CloudServerManager::getInstance()->startServer($template, ($template->getSettings()->getMinServerCount() - $running));
                }
            }

            if ($latest = CloudServerManager::getInstance()->getLatest($template)) {
                $players = $latest->getCloudPlayerCount();
                $requiredPercentage = $template->getSettings()->getStartNewPercentage();

                if ($requiredPercentage <= 0) {
                    return;
                }

                $percentage = ($players * 100) / $requiredPercentage;

                if ($percentage >= $requiredPercentage && CloudServerManager::getInstance()->canStartMore($template)) {
                    CloudServerManager::getInstance()->startServer($template);
                }
            }

        }
    }

    public function getTemplateByName(string $name): ?Template {
        return $this->templates[$name] ?? null;
    }

    public function getTemplates(): array {
        return $this->templates;
    }

    public static function getInstance(): self {
        return self::$instance ??= new self;
    }
}