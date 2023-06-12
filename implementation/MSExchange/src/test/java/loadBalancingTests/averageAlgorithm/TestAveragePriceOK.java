package loadBalancingTests.averageAlgorithm;

import MSP.Exceptions.PriceNotOKException;
import loadManager.prosumerActionManagement.AverageMechanism;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import CF.sendable.Bid;
import CF.sendable.Sell;

import java.util.UUID;

public class TestAveragePriceOK {
    private double volume = 100.0;
    private double price = 100.0;
    private UUID timeSlotID = UUID.randomUUID();


    @Test
    public void givenBidPricesAndAskPricesLowerThenKValues_isBidPriceHighEnough_expectedTrueAndAveragePriceIsZero() {

        //arrange
        AverageMechanism averageMechanism = new AverageMechanism();

        Sell sell = new Sell(volume, price, timeSlotID, UUID.randomUUID());
        try {
            averageMechanism.isAskPriceLowEnough(sell.getAskPrice());
        } catch (PriceNotOKException e) {
            throw new RuntimeException(e);
        }

        Bid bid = new Bid(volume, price, timeSlotID, UUID.randomUUID());

        //act - assert
        Assertions.assertDoesNotThrow(() -> averageMechanism.isBidPriceHighEnough(bid.getPrice()));

        try {
            Assertions.assertTrue(averageMechanism.isBidPriceHighEnough(bid.getPrice()));
        } catch (PriceNotOKException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertEquals(0.0, averageMechanism.getAveragePrice());
    }

    @Test
    public void givenBidPricesAndAskPricesHigherThenKValues_isBidPriceHighEnough_expectedTrue() {

        //arrange
        AverageMechanism averageMechanism = new AverageMechanism();

        for (int i = 0; i < 200; i++) {
            Sell sell = new Sell(volume, price, timeSlotID, UUID.randomUUID());
            try {
                averageMechanism.isAskPriceLowEnough(sell.getAskPrice());
            } catch (PriceNotOKException e) {
                throw new RuntimeException(e);
            }

            Bid bid = new Bid(volume, price, timeSlotID, UUID.randomUUID());
            try {
                averageMechanism.isBidPriceHighEnough(bid.getPrice());
            } catch (PriceNotOKException e) {
                throw new RuntimeException(e);
            }
        }


        Bid bid = new Bid(volume, price, timeSlotID, UUID.randomUUID());

        //act - assert
        try {
            Assertions.assertTrue(averageMechanism.isBidPriceHighEnough(bid.getPrice()));
        } catch (PriceNotOKException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertEquals(price, averageMechanism.getAveragePrice());
    }

    @Test
    public void givenBidLowerThenAveragePrice_isBidPriceHighEnough_expectedFalse() {

        //arrange
        AverageMechanism averageMechanism = new AverageMechanism();

        for (int i = 0; i < 200; i++) {
            try {
                Sell sell = new Sell(volume, price, timeSlotID, UUID.randomUUID());
                Bid bid = new Bid(volume, price, timeSlotID, UUID.randomUUID());

                averageMechanism.isAskPriceLowEnough(sell.getAskPrice());
                averageMechanism.isBidPriceHighEnough(bid.getPrice());
            } catch (PriceNotOKException e) {
                throw new RuntimeException(e);
            }
        }

        Bid bid = new Bid(volume, price - 1, timeSlotID, UUID.randomUUID());

        //act - assert
        try {
            Assertions.assertFalse(averageMechanism.isBidPriceHighEnough(bid.getPrice()));
        } catch (PriceNotOKException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertEquals(price, averageMechanism.getAveragePrice());
    }

    @Test
    public void givenBidNegativPrice_isBidPriceHighEnough_expectedPriceNotOKException() {
        //arrange
        AverageMechanism averageMechanism = new AverageMechanism();

        Bid bid = new Bid(volume, -1, timeSlotID, UUID.randomUUID());

        //act - assert
        Assertions.assertThrows(PriceNotOKException.class, () -> averageMechanism.isBidPriceHighEnough(bid.getPrice()));
    }

    @Test
    public void givenSellNegativPrice_isAskPriceLowEnough_expectedPriceNotOKException() {
        //arrange
        AverageMechanism averageMechanism = new AverageMechanism();

        Sell sell = new Sell(volume, -1, timeSlotID, UUID.randomUUID());

        //act - assert
        Assertions.assertThrows(PriceNotOKException.class, () -> averageMechanism.isAskPriceLowEnough(sell.getAskPrice()));
    }

    @Test
    public void givenBidsAndSells_getAveragePrice_ExpectedCorrectPrice() {
        //arrange
        AverageMechanism averageMechanism = new AverageMechanism();

        double averagePrice = 0.0;
        double highestBidPrice = 120;
        double highestSellPrice = 110;
        for (int i = 0; i < 200; i++) {
            Sell sell;
            if (i == 140) {
                sell = new Sell(volume, highestSellPrice, timeSlotID, UUID.randomUUID());
            } else {
                sell = new Sell(volume, price, timeSlotID, UUID.randomUUID());
            }

            try {
                averageMechanism.isAskPriceLowEnough(sell.getAskPrice());
            } catch (PriceNotOKException e) {
                throw new RuntimeException(e);
            }

            Bid bid;
            if (i == 130) {
                bid = new Bid(volume, highestBidPrice, timeSlotID, UUID.randomUUID());
            } else {
                bid = new Bid(volume, price, timeSlotID, UUID.randomUUID());
            }

            try {
                averageMechanism.isBidPriceHighEnough(bid.getPrice());
            } catch (PriceNotOKException e) {
                throw new RuntimeException(e);
            }
        }

        //act
        averagePrice = averageMechanism.getAveragePrice();

        //assert
        Assertions.assertEquals(115, averagePrice);
    }

}
