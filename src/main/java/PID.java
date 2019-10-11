public class PID {
    private double errorSum = 0;
    private double lastError = 0;
    private double error = 0;
    private double required = 0;

    private double pFactor = 0;
    private double iFactor = 0;
    private double dFactor = 0;
    private double MAX_ERRORSUM = 1000;

    public void setRequired(double r) {
        this.required = r;
    }

    public void setPID(double p, double i, double d) {
        this.pFactor = p;
        this.iFactor = i;
        this.dFactor = d;
    }

    public void setInput(double newActual) {
        this.error = this.required - newActual;
    }

    public double getError() {
        return this.error;
    }

    public double getErrorSum() {
        return this.errorSum;
    }

    public double getProportional() {
        return this.pFactor * this.error;
    }

    public double getIntegral() {
        double newSum = this.errorSum + this.error;
        if(!(newSum > MAX_ERRORSUM || newSum < -MAX_ERRORSUM)){
            this.errorSum = newSum;
        }
        return this.iFactor * this.errorSum;
    }

    public double getDifferencial() {
        double delta = this.lastError - this.error;
        this.lastError = this.error;
        return this.dFactor * delta;
    }

    public double getOutput() {
        double p = this.getProportional();
        double i = this.getIntegral();
        double d = this.getDifferencial();
        return p + i + d;
    }
}
