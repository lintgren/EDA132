package LAB1;

import java.util.ArrayList;
import java.util.List;

class Node{
    private List<Node> children = new ArrayList<Node>();
    private Node parentNode = null;
    public int x;
    public int y;
    protected Node(){

    }
    protected Node(int x,int y){
        this.x = x;
        this.y = y;
    }
    protected Node(int x, int y, Node parentNode){
        this.x = x;
        this.y = y;
        this.parentNode = parentNode;
    }
    public void addChild(Node child){
        children.add(child);
    }
    public boolean isRoot(){
        return (this.parentNode == null);
    }
    public boolean isLeaf(){
        if(children.size() == 0)
            return true;
        return false;
    }
    public void setParentNode(Node parent){
        parentNode = parent;
    }
}
