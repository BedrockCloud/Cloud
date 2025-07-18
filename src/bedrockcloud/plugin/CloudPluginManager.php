<?php

namespace bedrockcloud\plugin;

use bedrockcloud\event\EventManager;
use bedrockcloud\event\impl\plugin\PluginDisableEvent;
use bedrockcloud\event\impl\plugin\PluginEnableEvent;
use bedrockcloud\event\impl\plugin\PluginLoadEvent;
use bedrockcloud\language\Language;
use bedrockcloud\plugin\loader\FolderCloudPluginLoader;
use bedrockcloud\plugin\loader\PharCloudPluginLoader;
use bedrockcloud\plugin\loader\CloudPluginLoader;
use bedrockcloud\util\CloudLogger;
use bedrockcloud\util\Reloadable;
use bedrockcloud\util\SingletonTrait;
use bedrockcloud\util\Tickable;
use Throwable;

final class CloudPluginManager implements Tickable, Reloadable
{
    use SingletonTrait;

    /** @var array<string, CloudPlugin> */
    private array $plugins = [];
    /** @var array<string, CloudPlugin> */
    private array $enabledPlugins = [];
    /** @var array<CloudPluginLoader> */
    private array $loaders = [];

    public function __construct()
    {
        self::setInstance($this);
        $this->registerLoader(new PharCloudPluginLoader());
        $this->registerLoader(new FolderCloudPluginLoader());
    }

    public function registerLoader(CloudPluginLoader $loader): void
    {
        $this->loaders[] = $loader;
    }

    public function loadPlugins(): void
    {
        CloudLogger::get()->info(Language::current()->translate("plugins.loading"));

        $plugins = array_diff(scandir(CLOUD_PLUGINS_PATH), [".", ".."]);
        foreach ($plugins as $file) {
            $this->loadPlugin(CLOUD_PLUGINS_PATH . $file);
        }

        $pluginCount = count($this->plugins);
        CloudLogger::get()->info(
            $pluginCount === 0
                ? Language::current()->translate("plugins.loaded.none")
                : Language::current()->translate("plugins.loaded", $pluginCount)
        );
    }

    public function loadPlugin(string $path): void
    {
        CloudLogger::get()->info(Language::current()->translate("plugin.load", basename($path)));

        foreach ($this->loaders as $loader) {
            try {
                if ($loader->canLoad($path)) {
                    $plugin = $loader->loadPlugin($path);

                    if (!$plugin instanceof CloudPlugin) {
                        CloudLogger::get()->error(Language::current()->translate("plugin.loading.failed", basename($path), $plugin));
                        return;
                    }

                    if (isset($this->plugins[$plugin->getDescription()->getName()])) {
                        CloudLogger::get()->error(Language::current()->translate("plugin.loading.failed", $plugin->getDescription()->getName(), "Plugin already exists"));
                        return;
                    }

                    (new PluginLoadEvent($plugin))->call();
                    $this->plugins[$plugin->getDescription()->getName()] = $plugin;
                    $plugin->onLoad();
                }
            } catch (Throwable $exception) {
                CloudLogger::get()->error(Language::current()->translate("plugin.loading.failed", basename($path), $exception->getMessage()));
                CloudLogger::get()->exception($exception);
            }
        }
    }

    public function enablePlugins(): void
    {
        CloudLogger::get()->info(Language::current()->translate("plugins.enabling"));

        foreach ($this->plugins as $plugin) {
            $this->enablePlugin($plugin);
        }

        $enabledCount = count($this->enabledPlugins);
        CloudLogger::get()->info(
            $enabledCount === 0
                ? Language::current()->translate("plugins.enabled.none")
                : Language::current()->translate("plugins.enabled", $enabledCount)
        );
    }

    public function enablePlugin(CloudPlugin $plugin): void
    {
        CloudLogger::get()->info(Language::current()->translate("plugin.enabling", $plugin->getDescription()->getName(), $plugin->getDescription()->getFullName()));

        $plugin->setEnabled(true);
        (new PluginEnableEvent($plugin))->call();

        try {
            $plugin->onEnable();
        } catch (Throwable $throwable) {
            CloudLogger::get()->exception($throwable);
            $this->disablePlugin($plugin);
        }

        if ($plugin->isEnabled()) {
            $this->enabledPlugins[$plugin->getDescription()->getName()] = $plugin;
        }
    }

    public function disablePlugins(): void
    {
        foreach ($this->enabledPlugins as $plugin) {
            $this->disablePlugin($plugin);
        }

        CloudLogger::get()->info(Language::current()->translate("plugins.disabled"));
    }

    public function disablePlugin(CloudPlugin $plugin): void
    {
        CloudLogger::get()->info(Language::current()->translate("plugin.disabling", $plugin->getDescription()->getName(), $plugin->getDescription()->getFullName()));

        (new PluginDisableEvent($plugin))->call();
        $plugin->setEnabled(false);
        $plugin->onDisable();
        $plugin->getScheduler()->cancelAll();
        EventManager::getInstance()->removeHandlers($plugin);

        unset($this->enabledPlugins[$plugin->getDescription()->getName()]);
    }

    public function clear(): void
    {
        $this->plugins = [];
        $this->enabledPlugins = [];
    }

    public function reload(): bool
    {
        foreach ($this->plugins as $plugin) {
            if ($plugin->isEnabled()) {
                $this->disablePlugin($plugin);
            }
            unset($this->plugins[$plugin->getDescription()->getName()]);
        }

        $this->clear();

        $plugins = array_diff(scandir(CLOUD_PLUGINS_PATH), [".", ".."]);
        foreach ($plugins as $file) {
            $this->loadPlugin(CLOUD_PLUGINS_PATH . $file);
        }

        return true;
    }

    public function tick(int $currentTick): void
    {
        foreach ($this->enabledPlugins as $enabledPlugin) {
            if ($enabledPlugin->isEnabled()) {
                $enabledPlugin->getScheduler()->tick($currentTick);
            }
        }
    }

    public function getPluginByName(string $name): ?CloudPlugin
    {
        return $this->plugins[$name] ?? null;
    }

    public function getLoaders(): array
    {
        return $this->loaders;
    }

    public function getEnabledPlugins(): array
    {
        return $this->enabledPlugins;
    }

    public function getPlugins(): array
    {
        return $this->plugins;
    }

    public static function getInstance(): self
    {
        return self::$instance ??= new self;
    }
}