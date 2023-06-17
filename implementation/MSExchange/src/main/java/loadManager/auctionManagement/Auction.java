package loadManager.auctionManagement;

import CF.sendable.Bid;
import CF.sendable.Transaction;
import MSP.Exceptions.CommandNotPossibleException;
import MSP.Exceptions.InvalidBidException;
import loadManager.SellInformation;

import java.util.UUID;

public class Auction {
    private final UUID AUCTION_ID;
    private final UUID SELLER_ID;
    private final UUID TIMESLOT_ID;
    private final double TOTAL_VOLUME;
    private final UUID EXCHANGE_ID;
    private UUID bidderID;
    private double pricePerWh;
    private double coveredVolume = 0;
    private boolean auctionEnded = false;

    public Auction(UUID auctionID, SellInformation sellPosition) {
        this.AUCTION_ID = auctionID;
        this.SELLER_ID = sellPosition.getSell().getSellerID();
        this.pricePerWh = sellPosition.getSell().getAskPrice();
        this.TOTAL_VOLUME = sellPosition.getSell().getVolume();
        this.TIMESLOT_ID = sellPosition.getSell().getTimeSlot();
        this.EXCHANGE_ID = sellPosition.getExchangeID();
    }

    public void setBid(Bid bidPosition) throws InvalidBidException {
        if (!auctionEnded) {
            if (this.bidderID != null) {
                if (bidPosition.getPrice() > pricePerWh) {
                    if (bidPosition.getVolume() <= this.TOTAL_VOLUME) {
                        this.coveredVolume = bidPosition.getVolume();
                    } else {
                        throw new InvalidBidException("LOAD_MANAGER: Bid volume is higher than sell volume", bidPosition);
                    }
                    this.pricePerWh = bidPosition.getPrice();
                    this.bidderID = bidPosition.getBidderID();
                }
            } else {
                if (bidPosition.getVolume() <= this.TOTAL_VOLUME) {
                    this.coveredVolume = bidPosition.getVolume();
                } else {
                    throw new InvalidBidException("LOAD_MANAGER: Bid volume is higher than sell volume", bidPosition);
                }
                this.pricePerWh = bidPosition.getPrice();
                this.bidderID = bidPosition.getBidderID();

            }
        }
    }

    public void endAuction() {
        auctionEnded = true;
    }

    public Transaction getTransaction() throws CommandNotPossibleException {
        if (!auctionEnded) {
            throw new CommandNotPossibleException("LOAD_MANAGER: Auction has not ended yet");
        }

        return new Transaction(SELLER_ID, bidderID, TOTAL_VOLUME, pricePerWh, AUCTION_ID);
    }

    public double getPrice() {
        return pricePerWh;
    }

    public double getCoveredVolume() {
        return coveredVolume;
    }

    public UUID getAuctionId() {
        return AUCTION_ID;
    }

    public UUID getBidderID() {
        return bidderID;
    }

    public double getTOTAL_VOLUME() {
        return this.TOTAL_VOLUME;
    }

    public UUID getSELLER_ID() {
        return this.SELLER_ID;
    }

    public UUID getTIMESLOT_ID() {
        return this.TIMESLOT_ID;
    }

    public UUID getEXCHANGE_ID() {
        return this.EXCHANGE_ID;
    }
}
