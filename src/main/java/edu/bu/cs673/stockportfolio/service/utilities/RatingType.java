package edu.bu.cs673.stockportfolio.service.utilities;

/**********************************************************************************************************************
 * A mapping of current and historical consensus analyst recommendations and price targets
 *********************************************************************************************************************/
public enum RatingType {
    STRONG_BUY(100F, Float.MAX_VALUE),
    BUY(50F, 99F),
    HOLD(-49F, 49F),
    SELL(-99F, -50F),
    STRONG_SELL(Float.MIN_VALUE, -100F);

    private final Float minimum;
    private final Float maximum;

    RatingType(Float minimum, Float maximum) {
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public Float getMaximum() {
        return maximum;
    }

    public Float getMinimum() {
        return minimum;
    }
}
