package fr.projetl3.deltapp;

public class Modules {
    private String moduleName;
    private String description;

    public Modules(String moduleName) {
        this.moduleName= moduleName;
        setDesc();
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    private void setDesc(){

    }
}
