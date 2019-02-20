package ink.plink.plinkApp.filter;

import java.util.ArrayList;

import ink.plink.plinkApp.databaseObjects.Printer;

public class FilterParams {

    private boolean showInactive;
    private boolean showColorOnly;
    private double lowPrice;
    private double highPrice;

    private ArrayList<Printer> printers;

    public FilterParams() {
        this.showInactive = false;
        this.showColorOnly = false;
        this.lowPrice = Double.MIN_VALUE;
        this.highPrice = Double.MAX_VALUE;
    }

    public FilterParams setPrinters(ArrayList<Printer> printers) {
        this.printers = printers;
        return this;
    }

    public ArrayList<Printer> getPrinters() {
        return this.printers;
    }

    public boolean isShowInactive() {
        return this.showInactive;
    }

    public void setShowInactive(boolean b) {
        this.showInactive = b;
    }

    public boolean isShowColorOnly() {
        return this.showColorOnly;
    }

    public void setShowColorOnly(boolean b) {
        this.showColorOnly = b;
    }

    public void setLowPrice(double price) {
        this.lowPrice = price;
    }

    public void setHighPrice(double price) {
        this.highPrice = price;
    }

    public boolean isInPriceRange(double price) {
        return (price >= this.lowPrice && price <= this.highPrice);
    }

    public ArrayList<Printer> getFilteredPrinters(ArrayList<Printer> printers) {
        for(Printer printer : printers) {
            if (!printer.getStatus() && this.isShowInactive()) {
                printers.remove(printer);
                break;
            }
            if (!printer.getColor() && this.isShowColorOnly()) {
                printers.remove(printer);
                break;
            }
            if (!isInPriceRange(printer.getPrice()) && !isInPriceRange(printer.getColorPrice())) {
                printers.remove(printer);
                break;
            }
        }
        return printers;
    }
}
