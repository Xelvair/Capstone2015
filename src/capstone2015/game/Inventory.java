package capstone2015.game;

import capstone2015.entity.Item;

public class Inventory {
    
    private Item[] items;

    public Inventory(int size){
        items = new Item[size];
    }
    
    public void resize(int size){
        Item[] new_items = new Item[size];
        for(int i = 0; i < items.length & i < new_items.length; i++){
            new_items[i] = items[i];
            items = new_items;
        }
    }
    
    public int size(){
        return items.length;
    }
    
    public Item get(int idx){
        return items[idx];
    }
    
    public void set(int idx, Item item){
        items[idx] = item;
    }
    
    public boolean add(Item item){
        for(int i = 0; i < size(); i++){
            if(items[i] == null){
                items[i] = item;
                return true;
            }
        }
        System.out.println("Failed to add item to full inventory!");
        return false;
    }
    
    public int freeSlotsCount(){
        int free_slots = 0;
    
        for(int i = 0; i < size(); i++){
            if(items[i] == null){
                free_slots++;
            }
        }
        
        return free_slots;
    }
    
    public Item take(int idx){
        Item item = items[idx];
        items[idx] = null;
        return item;
    }

    public void remove(Item item){
        for(int i = 0; i < items.length; i++){
            if(items[i] == item){
                items[i] = null;
            }
        }
    }
}
