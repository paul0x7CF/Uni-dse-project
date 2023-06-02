package exchangeTests.network;

import broker.BrokerRunner;
import msExchange.messageHandling.MessageBuilder;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import protocol.ECategory;
import protocol.Message;
import sendable.EServiceType;
import sendable.MSData;
import sendable.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;

public class TestMessageBuilder {

    @Test
    public void givenTransaction_buildMessage_expectedCorrectMessages() {
        EServiceType serviceType = EServiceType.ExchangeWorker;
        int port = 0000;

        BrokerRunner brokerMock = Mockito.mock(BrokerRunner.class);
        MessageBuilder messageBuilder = new MessageBuilder(brokerMock);

        MSData sellerMSData = new MSData(UUID.randomUUID(), EServiceType.Prosumer, "", 1111);
        MSData bidderMS = new MSData(UUID.randomUUID(), EServiceType.Prosumer, "", 2222);
        List<MSData> storageMSDataList = new ArrayList<>();
        storageMSDataList.add(new MSData(UUID.randomUUID(), EServiceType.Storage, "", 3333));

        when(brokerMock.getCurrentService()).thenReturn(new MSData(UUID.randomUUID(), serviceType, "", port));
        when(brokerMock.findService(sellerMSData.getId())).thenReturn(sellerMSData);
        when(brokerMock.findService(bidderMS.getId())).thenReturn(bidderMS);
        when(brokerMock.getServicesByType(EServiceType.Storage)).thenReturn(storageMSDataList);

        //Create a Transaction object for testing
        Transaction transaction = new Transaction(sellerMSData.getId(), bidderMS.getId(), 12, 12, UUID.randomUUID());

        //Call the buildMessage() method and verify the results
        List<Message> messages = messageBuilder.buildMessage(transaction);

        boolean sellerExists = false;
        boolean bidderExists = false;
        boolean storageExists = false;
       
        for (Message message : messages) {
            if (message.getReceiverID() == sellerMSData.getId()) {
                sellerExists = true;
            }
            if (message.getReceiverID() == bidderMS.getId()) {
                bidderExists = true;
            }
            if (message.getReceiverID() == storageMSDataList.get(0).getId()) {
                storageExists = true;
            }
            Assertions.assertEquals(ECategory.Auction, message.getMainCategory());
            Assertions.assertEquals("Transaction", message.getSubCategory());
            Transaction transactionFromMessage = (Transaction) message.getSendable(Transaction.class);
            Assertions.assertEquals(transaction.getTransactionID(), transactionFromMessage.getTransactionID());
            Assertions.assertEquals(transaction.getBuyerID(), transactionFromMessage.getBuyerID());
            Assertions.assertEquals(transaction.getSellerID(), transactionFromMessage.getSellerID());

        }
        Assertions.assertEquals(true, sellerExists);
        Assertions.assertEquals(true, bidderExists);
        Assertions.assertEquals(true, storageExists);

    }
}
