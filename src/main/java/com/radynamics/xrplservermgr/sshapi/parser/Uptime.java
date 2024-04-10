package com.radynamics.xrplservermgr.sshapi.parser;

public class Uptime {
    private double past1min;
    private double past5min;
    private double past15min;

    public static Uptime parse(String line) {
        // line eg. " 16:23:08 up 4 min,  1 user,  load average: 0,03, 0,08, 0,04"
        var loadAverageLabel = "load average:";
        var loadAverage = line.substring(line.indexOf(loadAverageLabel) + loadAverageLabel.length()).trim().split(" ");
        var o = new Uptime();
        o.past1min(toDouble(loadAverage[0].substring(0, loadAverage[0].length() - 1)));
        o.past5min(toDouble(loadAverage[1].substring(0, loadAverage[1].length() - 1)));
        o.past15min(toDouble(loadAverage[2]));
        return o;
    }

    private static double toDouble(String value) {
        var withDots = value.replaceAll(",", ".");
        return Double.parseDouble(withDots);
    }

    public Double past1min() {
        return past1min;
    }

    private void past1min(double value) {
        this.past1min = value;
    }

    public Double past5min() {
        return past5min;
    }

    private void past5min(double value) {
        this.past5min = value;
    }

    public Double past15min() {
        return past15min;
    }

    private void past15min(double value) {
        this.past15min = value;
    }
}
