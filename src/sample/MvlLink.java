package sample;

class MvlLink {
    private String name;
    private String source;
    private String destination;

    MvlLink(String name, String source, String destination){
        this.name=name;
        this.destination=destination;
        this.source=source;
    }


    public String getName() {
        return name;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    @Override
    public String toString(){
        return "link("+getName()+") "+getSource()+", "+getDestination();
    }
}
