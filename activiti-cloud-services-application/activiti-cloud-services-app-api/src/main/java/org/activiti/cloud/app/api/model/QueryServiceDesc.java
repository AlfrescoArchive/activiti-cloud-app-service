package org.activiti.cloud.app.api.model;

public class QueryServiceDesc implements ServiceDesc {

    private String group;
    private String name;
    private String version;
    private TYPE type;

    public QueryServiceDesc() {
    }

    public QueryServiceDesc(String group,
                            String name,
                            String version,
                            TYPE type) {
        this.group = group;
        this.name = name;
        this.version = version;
        this.type = type;
    }

    @Override
    public String getId() {
        return group + ":" + name + ":" + version;
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public TYPE getType() {
        return type;
    }
}
