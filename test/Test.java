package test;

import cz.mg.resourcemanager.ResourceManager;


class PhantomasAllocator {
    private static int i = 1;
    private static int numberOfAllocatedObjects = 0;

    public static Integer alloc(){
        Integer address = i++;
        numberOfAllocatedObjects++;
        System.out.println("MALLOC " + address);
        return address;
    }

    public static void free(Integer address) {
        if(address == null) return;
        numberOfAllocatedObjects--;
        //if(address == 3) throw new RuntimeException("I refuse to free!!!");
        System.out.println("FREE " + address);
    }
}

class Phantomas implements AutoCloseable {
    private Integer address;

    public Phantomas() {
        this.address = PhantomasAllocator.alloc();
    }

    @Override
    public void close() {
        PhantomasAllocator.free(address);
        address = null;
    }
}

public class Test {
    public static void main(String... args) {
        ResourceManager resourceManager = new ResourceManager();

        Object owner1 = new Object();
        Object owner2 = new Object();

        Phantomas p1;
        Phantomas p2;
        Phantomas p3;
        Phantomas p4;
        Phantomas p5;

        resourceManager.add(owner1, p1 = new Phantomas());
        resourceManager.add(owner1, p2 = new Phantomas(), p1);
        resourceManager.add(owner2, p3 = new Phantomas(), p2);
        resourceManager.add(owner1, p4 = new Phantomas(), p3);
        resourceManager.add(owner1, p5 = new Phantomas(), p4);

        owner1 = null;
        owner2 = null;

        System.gc();
        while(resourceManager.count() > 0) resourceManager.free(true);
    }
}
