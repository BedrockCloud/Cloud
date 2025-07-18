<?php

namespace bedrockcloud\network;

use Exception;
use pmmp\thread\ThreadSafeArray;
use bedrockcloud\config\impl\DefaultConfig;
use bedrockcloud\event\impl\network\NetworkBindEvent;
use bedrockcloud\event\impl\network\NetworkCloseEvent;
use bedrockcloud\event\impl\network\NetworkPacketReceiveEvent;
use bedrockcloud\event\impl\network\NetworkPacketSendEvent;
use bedrockcloud\language\Language;
use bedrockcloud\network\client\ServerClient;
use bedrockcloud\network\client\ServerClientManager;
use bedrockcloud\network\packet\CloudPacket;
use bedrockcloud\network\packet\handler\PacketSerializer;
use bedrockcloud\network\packet\UnhandledPacketObject;
use bedrockcloud\BedrockCloud;
use bedrockcloud\thread\Thread;
use bedrockcloud\util\Address;
use bedrockcloud\util\CloudLogger;
use bedrockcloud\util\SingletonTrait;
use pocketmine\snooze\SleeperHandlerEntry;
use Socket;

final class Network extends Thread
{
    use SingletonTrait;

    private ?SleeperHandlerEntry $entry = null;
    private ThreadSafeArray $buffer;
    private ?Socket $socket = null;
    private bool $connected = false;

    public function __construct(private Address $address)
    {
        self::setInstance($this);
        $this->buffer = new ThreadSafeArray();
    }

    public function onRun(): void
    {
        while ($this->isConnected() && $this->isRunning()) {
            if ($this->read($buffer, $address, $port)) {
                $this->buffer[] = new UnhandledPacketObject($buffer, $address, $port);
                $this->entry->createNotifier()->wakeupSleeper();
            }
        }
    }

    public function init(): void
    {
        CloudLogger::get()->info(Language::current()->translate("network.bind", $this->address->__toString()));

        if (!$this->bind($this->address)) {
            CloudLogger::get()->error(Language::current()->translate("network.bind.failed", $this->address->__toString()));
            BedrockCloud::getInstance()->shutdown();
            return;
        }

        CloudLogger::get()->info(Language::current()->translate("network.bound", $this->address->__toString()));

        $this->entry = BedrockCloud::getInstance()->getSleeperHandler()->addNotifier(function (): void {
            while (($object = $this->buffer->shift()) !== null) {
                $this->processReceivedPacket($object);
            }
        });
    }

    private function bind(Address $address): bool
    {
        if ($this->connected) {
            return false;
        }

        $this->address = $address;
        $this->socket = @socket_create(AF_INET, SOCK_DGRAM, SOL_UDP);

        if (!$this->socket || !socket_bind($this->socket, $address->getAddress(), $address->getPort())) {
            return false;
        }

        $this->connected = true;
        $this->setSocketOptions();
        (new NetworkBindEvent($this->address))->call();
        return true;
    }

    private function setSocketOptions(): void
    {
        if ($this->socket) {
            socket_set_option($this->socket, SOL_SOCKET, SO_SNDBUF, 1024 * 1024 * 8);
            socket_set_option($this->socket, SOL_SOCKET, SO_RCVBUF, 1024 * 1024 * 8);
        }
    }

    private function processReceivedPacket(UnhandledPacketObject $object): void
    {
        $buffer = $object->getBuffer();
        $address = new Address($object->getAddress(), $object->getPort());
        $client = ServerClientManager::getInstance()->getClientByAddress($address) ?? new ServerClient($address);

        if (DefaultConfig::getInstance()->isNetworkOnlyLocal() && !$address->isLocalHost()) {
            CloudLogger::get()->warn(Language::current()->translate("network.receive.external", $client->getAddress()->__toString()));
            return;
        }

        try {
            if (($packet = PacketSerializer::decode($buffer)) !== null) {
                (new NetworkPacketReceiveEvent($packet, $client))->call();
                $packet->handle($client);
            } else {
                CloudLogger::get()->warn(Language::current()->translate("network.receive.unknown", $client->getAddress()->__toString()))
                    ->debug(DefaultConfig::getInstance()->isNetworkEncryptionEnabled() ? base64_decode($buffer) : $buffer);
            }
        } catch (Exception $e) {
            CloudLogger::get()->error("§cFailed to decode a packet!");
            CloudLogger::get()->debug($buffer);
            CloudLogger::get()->exception($e);
        }
    }

    public function write(string $buffer, Address $dst): bool
    {
        if (!$this->isConnected()) {
            return false;
        }

        return socket_sendto($this->socket, $buffer, strlen($buffer), 0, $dst->getAddress(), $dst->getPort()) !== false;
    }

    public function read(?string &$buffer, ?string &$address, ?int &$port): bool
    {
        if (!$this->isConnected()) {
            return false;
        }

        return socket_recvfrom($this->socket, $buffer, 65535, 0, $address, $port) !== false;
    }

    public function close(): void
    {
        if ($this->isConnected()) {
            (new NetworkCloseEvent())->call();
            $this->connected = false;
            $this->quit();
        }
    }

    public function sendPacket(CloudPacket $packet, ServerClient $client): bool
    {
        $buffer = PacketSerializer::encode($packet);
        $success = $this->write($buffer, $client->getAddress());
        (new NetworkPacketSendEvent($packet, $client, $success))->call();
        return $success;
    }

    public function broadcastPacket(CloudPacket $packet, ServerClient ...$excluded): void
    {
        foreach (ServerClientManager::getInstance()->getClients() as $client) {
            if (!in_array($client, $excluded, true)) {
                $this->sendPacket(clone $packet, $client);
            }
        }
    }

    public function isConnected(): bool
    {
        return $this->connected;
    }

    public function getAddress(): Address
    {
        return $this->address;
    }

    public static function getInstance(): ?self
    {
        return self::$instance;
    }
}