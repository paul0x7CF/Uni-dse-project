# Verwendung der Library

Um die Broker-Bibliothek in den anderen Komponenten zu nutzen, befolgen Sie die folgenden Schritte:

## Instantiierung des Brokers
- Siehe **MainForTesting.java**

Erstellen Sie zuerst eine Instanz des Brokers. Die Instanziierung ist unkompliziert und kann direkt erstellt werden:

``` java
BrokerRunner broker = new BrokerRunner(EServiceType, listeningPort);
```

## MessageHandler-Klassen
- siehe Konstruktor von **Broker.java** und **InfoMessageHandler.java**

Erstellen Sie Ihre eigenen MessageHandler-Klassen für Auction, Exchange und Forecast. Diese Klassen sollten eine Instanz
von IBroker enthalten, um Nachrichten zu senden. Hier ist ein einfaches Beispiel für eine solche Klasse:

Zu beachten ist hierbei, dass lokale Klassen wir z.B. Prosumer auch als Feld in der MessageHandler-Klasse gespeichert
werden können.

``` java
public class AuctionMessageHandler implements IMessageHandler {
private IBroker broker;

    public AuctionMessageHandler(IBroker broker) {
        this.broker = broker;
    }

    @Override
    public void handle(Message message) {
        // Hier Nachricht bearbeiten und ggf. neue Nachrichten senden
    }
}
```

Machen Sie dasselbe für ExchangeMessageHandler und ForecastMessageHandler. Fügen Sie dann alle diese Handler zum Broker
hinzu:

``` java
broker.addMessageHandler(new AuctionMessageHandler(broker));
broker.addMessageHandler(new ExchangeMessageHandler(broker));
broker.addMessageHandler(new ForecastMessageHandler(broker));
```

Bitte beachten Sie, dass der Broker nicht startet, bevor alle MessageHandler hinzugefügt wurden.

### Die Businesslogik der Komponente sollte in den MessageHandler-Klassen implementiert werden
Es ist außerdem wichtig zu verstehen, dass in diesen Klassen die Nachrichten verarbeitet werden. Wenn Sie also eine
Nachricht erhalten, die Sie verarbeiten möchten, müssen Sie dies in der handle()-Methode tun. Wenn Sie eine neue
Nachricht senden möchten, können Sie dies mit der send()-Methode tun, die in der IBroker-Schnittstelle definiert ist.


## Klasse zum Erstellen von Nachrichten
- siehe **InfoMessageBuilder.java**

Um die Klarheit des Codes zu bewahren, sollten Sie eine spezielle Klasse zum Erstellen von Nachrichten erstellen. Diese
Klasse könnte eine Reihe von Methoden enthalten, die verschiedene Arten von Nachrichten erstellen, abhängig von den
übergebenen Parametern. Ein Beispiel für eine solche Klasse könnte so aussehen:

``` java
private static MessageFactory senderAndReceiverTemplate(MSData sender, MSData receiver) {
        MessageFactory messageFactory = new MessageFactory();
        return messageFactory.setSenderID(sender.getId()).
            setSenderAddress(sender.getAddress()).
            setSenderPort(sender.getPort()).
            setReceiverID(receiver.getId()).
            setReceiverAddress(receiver.getAddress()).
            setReceiverPort(receiver.getPort());
}
```

## Starten Sie den Broker
- siehe **MainForTesting.java**

Schließlich müssen Sie den Broker starten. Da Broker das Runnable-Interface implementiert, kann dies einfach durch
Aufruf der start()-Methode erreicht werden:

``` java
broker.run();
```

Jetzt sollte Ihr Broker bereit sein, Nachrichten zu senden und zu empfangen. Erstellen Sie Nachrichten mit Ihrer
MessageBuilder-Klasse und senden Sie sie über den Broker.