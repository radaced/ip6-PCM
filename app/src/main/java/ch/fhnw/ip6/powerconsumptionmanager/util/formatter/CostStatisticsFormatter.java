package ch.fhnw.ip6.powerconsumptionmanager.util.formatter;

import com.github.mikephil.charting.formatter.StackedValueFormatter;


public class CostStatisticsFormatter extends StackedValueFormatter {

    /**
     * Constructor.
     *
     * @param drawWholeStack if true, all stack values of the stacked bar entry are drawn, else only top
     * @param appendix       a string that should be appended behind the value
     * @param decimals       the number of decimal digits to use
     */
    public CostStatisticsFormatter(boolean drawWholeStack, String appendix, int decimals) {
        super(drawWholeStack, appendix, decimals);
    }
}
