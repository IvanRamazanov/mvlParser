package sample;

import java.util.ArrayList;
import java.util.List;

public class MvlClass {
    List<MvlLink> links;
    private String name;
    List<Cluster> clusters;

    public MvlClass(String name){
        links=new ArrayList<>();
        clusters =new ArrayList<>();
        this.name=name;
    }

    public void clustering(){
        if(!links.isEmpty()){
            clusters.add(new Cluster(links.get(0)));
            loop:
            for(int i=1;i<links.size();i++){
                MvlLink link=links.get(i);
                for(int j=0;j<clusters.size();j++){
                    Cluster clstr=clusters.get(j);
                    String d=link.getDestination();
                    String s=link.getSource();
                    if(clstr.contains(d) || clstr.contains(s)){
                        clstr.add(link);
                        continue loop;
                    }
                }
                clusters.add(new Cluster(link));
            }
        }
    }

    public List<MvlLink> checkClusters(){
        List<MvlLink> out=new ArrayList<>();
        for(int i=0;i<clusters.size();i++){
            Cluster clstr=clusters.get(i);
            if(clstr.isBad()){
                List<MvlLink> badLinks=clstr.findBadLinks();
                out.addAll(badLinks);
            }
        }
        return out;
    }

    public void addLink(MvlLink link){
        links.add(link);
    }

    public String getName() {
        return name;
    }

    class Cluster{
        List<MvlLink> links;
        List<String> nodes;

        Cluster(MvlLink link){
            links=new ArrayList<>();
            nodes=new ArrayList<>();

            links.add(link);
            nodes.add(link.getDestination());
            nodes.add(link.getSource());
        }

        /**
         * You definitely want to add.
         * @param link
         */
        void add(MvlLink link){
            this.links.add(link);
            if(!contains(link.getDestination())){
                this.nodes.add(link.getDestination());
            }
            if(!contains(link.getSource())){
                this.nodes.add(link.getSource());
            }
        }

        @Override
        public String toString(){
            StringBuilder str=new StringBuilder();
            for(String s:nodes){
                str.append(s+", ");
            }
            return "[ "+str.toString()+" ]";
        }

        boolean contains(String node){
            for(String s:nodes){
                if(node.equals(s))
                    return true;
            }
            return false;
        }

        List<String> getNodes(){
            return nodes;
        }

        boolean isBad(){
            return links.size()+1!=nodes.size();
        }

        List<MvlLink> findBadLinks(){
            int properSize=nodes.size()-1;
            if(properSize>=links.size())
                throw new Error("Hmmm, strange. Cluster is bad, but delta <=0");
            List<MvlLink> looserList=new ArrayList<>();

            //TODO not shure about get(0)
            MvlLink baseLink=links.get(0);
            Cluster goodCluster=new Cluster(links.get(0));

            loop:
            for(int i=1;i<links.size();i++){
                //perform combinatorics
                MvlLink testedLink=links.get(i);
                String d=testedLink.getDestination(),
                        s=testedLink.getSource();
                if(goodCluster.contains(d) && goodCluster.contains(s)){
                    // find useless link
                    looserList.add(testedLink);
                    continue loop;
                }else{
                    goodCluster.add(testedLink);
                }
            }
            if(!isNodesEqual(goodCluster.getNodes(),nodes)){
                throw new Error("Can't find good cluster?!");
            }
            return looserList;
        }

        boolean isNodesEqual(List<String> nodes1,List<String> nodes2){
            if(nodes1.size()!=nodes2.size())
                throw new Error("Sizes of nodes list don't match");
           // boolean out=true;
            loop:
            for(int i=0;i<nodes1.size();i++){
                for(int j=0;j<nodes2.size();j++){
                    if(nodes1.get(i).equals(nodes2.get(j))){
                        continue loop;
                    }
                }
                //out=false;
                return false;
            }
            return true;
        }
    }
}
