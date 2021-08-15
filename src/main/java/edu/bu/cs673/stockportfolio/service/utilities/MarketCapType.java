package edu.bu.cs673.stockportfolio.service.utilities;

public enum MarketCapType {
    MICROCAP(50000000L, 300000000L),
    SMALLCAP(300000000L, 2000000000L),
    MIDCAP(2000000000L, 10000000000L),
    LARGECAP(10000000000L, 200000000000L),
    MEGACAP(200000000000L,Long.MAX_VALUE);

    private final long minimum;
    private final long maximum;

    private MarketCapType(long minimum, long maximum) {
        this.minimum = minimum;
        this.maximum = maximum; 
    }

    public long getMaximum() {
        return maximum;
    }

    public long getMinimum() {
        return minimum;
    }
}

