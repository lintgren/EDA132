package LAB1;

import java.util.ArrayList;
import java.util.List;

class Node{
    private List<Node> children;
    private Node parentNode = null;
    public int x;
    public int y;
    public int depth;
    public int score;
    protected Node(){
        children = new ArrayList<>();
    }
    protected Node(int x,int y){
        this.x = x;
        this.y = y;
        children = new ArrayList<>();
    }
    protected Node(int x, int y, Node parentNode){
        this.x = x;
        this.y = y;
        children = new ArrayList<>();
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
    public List<Node> getChildren(){
        return children;
    }
}
