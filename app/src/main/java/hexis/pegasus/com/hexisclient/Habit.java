package hexis.pegasus.com.hexisclient;

public class Habit {
    private String habitName;
    private boolean badHabit;
    private int hourToGiveReminer;
    private int minuteToGiveReminder;
    private boolean zapSwitchState;
    private int noOfTimesToZap;
    private boolean beepSwitchState;
    private int noOfTimesToBeep;


    public String getHabitName() {
        return habitName;
    }

    public void setHabitName(String habitName) {
        this.habitName = habitName;
    }

    public boolean isBadHabit() {
        return badHabit;
    }

    public void setBadHabit(boolean badHabit) {
        this.badHabit = badHabit;
    }

    public boolean isZapSwitchState() {
        return zapSwitchState;
    }

    public void setZapSwitchState(boolean zapSwitchState) {
        this.zapSwitchState = zapSwitchState;
    }

    public int getNoOfTimesToZap() {
        return noOfTimesToZap;
    }

    public void setNoOfTimesToZap(int noOfTimesToZap) {
        this.noOfTimesToZap = noOfTimesToZap;
    }

    public boolean isBeepSwitchState() {
        return beepSwitchState;
    }

    public void setBeepSwitchState(boolean beepSwitchState) {
        this.beepSwitchState = beepSwitchState;
    }

    public int getNoOfTimesToBeep() {
        return noOfTimesToBeep;
    }

    public void setNoOfTimesToBeep(int noOfTimesToBeep) {
        this.noOfTimesToBeep = noOfTimesToBeep;
    }

    public int getHourToGiveReminer() {
        return hourToGiveReminer;
    }

    public void setHourToGiveReminer(int hourToGiveReminer) {
        this.hourToGiveReminer = hourToGiveReminer;
    }

    public int getMinuteToGiveReminder() {
        return minuteToGiveReminder;
    }

    public void setMinuteToGiveReminder(int minuteToGiveReminder) {
        this.minuteToGiveReminder = minuteToGiveReminder;
    }
}
