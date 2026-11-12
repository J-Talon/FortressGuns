package me.camm.productions.fortressguns.Util;

public enum Version {

    V1_17_1("v1_17_R1");

    private String versionCode;

    private Version(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionCode() {
        return versionCode;
    }
}
