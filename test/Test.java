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

        Phantomas p1 = new Phantomas();
        Phantomas p2 = new Phantomas();
        Phantomas p3 = new Phantomas();
        Phantomas p4 = new Phantomas();
        Phantomas p5 = new Phantomas();

        resourceManager.add(owner1, p1);
        resourceManager.add(owner1, p2);
        resourceManager.add(owner2, p3);
        resourceManager.add(owner1, p4);
        resourceManager.add(owner1, p5);

        resourceManager.add(p2, p1);
        resourceManager.add(p3, p2);
        resourceManager.add(p4, p3);
        resourceManager.add(p5, p4);

        Phantomas px = new Phantomas();
        resourceManager.add(owner1, px);
        resourceManager.add(owner1, px);

        p1 = null;
        p2 = null;
        p3 = null;
        p4 = null;
        p5 = null;
        owner1 = null;
        owner2 = null;

        resourceManager.waitFreeAll();
    }
}
