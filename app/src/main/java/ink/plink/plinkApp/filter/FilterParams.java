package ink.plink.plinkApp.filter;

import java.util.ArrayList;

import ink.plink.plinkApp.databaseObjects.Printer;

public class FilterParams {

    private boolean showInactive;
    private boolean showColorOnly;
    private double highPrice;

    private ArrayList<Printer> printers;

    public FilterParams() {
        this.showInactive = false;
        this.showColorOnly = false;
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

    public void setHighPrice(double price) {
        this.highPrice = price;
    }

    public double getHighPrice() {
        if (this.highPrice == Double.MAX_VALUE) {
            return 0.0;
        } else {
            return this.highPrice;
        }
    }

    @SuppressWarnings("unchecked")
    public ArrayList<Printer> getFilteredPrinters(ArrayList<Printer> printers) {
        ArrayList<Printer> printerList = (ArrayList<Printer>)printers.clone();
        for(Printer printer : printers) {
            if (!printer.getStatus() && !this.isShowInactive()) {
                printerList.remove(printer);
            }
            else if (!printer.getColor() && this.isShowColorOnly()) {
                printerList.remove(printer);
            }
            else if (printer.getColorPrice() > this.highPrice) {
                printerList.remove(printer);
            }
        }
        return printerList;
    }
}
