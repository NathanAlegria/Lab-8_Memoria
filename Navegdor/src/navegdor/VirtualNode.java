/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package navegdor;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author croge
 */
public class VirtualNode {

    private VirtualNode parent;
    private String name;
    private List<VirtualNode> children = new ArrayList<>();
    private boolean directory;
    private long size;
    private long lastModified;

    public VirtualNode(String name, boolean directory, long size, long lastModified) {
        this.name = name;
        this.size = size;
        this.directory = directory;
        this.lastModified = lastModified;
    }

    public String getName() {
        return name;
    }
    
    public VirtualNode getParent() {
        return parent;
    }
    
    
    public long getSize() {
        return size;
    }
    
    public List<VirtualNode> getChildren() {
        return children;
    }
    
    
    public long getLastModified() {
        return lastModified;
    }

    public void setName(String n) {
        this.name = n;
    }

    public boolean isDirectory() {
        return directory;
    }


    public void setLastModified(long t) {
        this.lastModified = t;
    }


    public void setParent(VirtualNode p) {
        this.parent = p;
    }


    public void setChildren(List<VirtualNode> c) {
        this.children = c;
    }

    public void addChild(VirtualNode child) {
        child.setParent(this);
        children.add(child);
    }

    public void removeChild(VirtualNode child) {
        children.remove(child);
    }

    public boolean hasChild(String childName) {
        for (VirtualNode c : children) {
            if (c.getName().equalsIgnoreCase(childName)) {
                return true;
            }
        }
        return false;
    }

    public void setSize(long s) {
        this.size = s;
    }

}
