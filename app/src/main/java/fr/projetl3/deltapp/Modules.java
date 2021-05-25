package fr.projetl3.deltapp;

public class Modules {
    private String moduleName;
    private int picModule;

    public Modules(String moduleName, String picModulePath) {
        this.moduleName= moduleName;
        this.picModule = picModule;
    }

    public String getModuleName() {
        return moduleName;
    }

    public int getPicModule() {
        return picModule;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public void setPicModule(int picModule) {
        this.picModule = picModule;
    }
}
