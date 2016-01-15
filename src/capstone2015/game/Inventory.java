package capstone2015.game;

import capstone2015.entity.Item;

public class Inventory {
    
    private Item[] items;
    private int select_idx = -1;
    
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
    
    public boolean setSelectIndex(int idx){
        if(idx < items.length && items[idx] != null){
            select_idx = idx;
            return true;
        } else {
            return false;
        }
    }
    
    public Item getSelectedItem(){
        if(0 <= select_idx && select_idx < items.length){
            return items[select_idx];
        } else {
            return null;
        }
    }

    public int findById(int id){
        for(int i = 0; i < items.length; ++i){
            if(items[i] != null && items[i].getProto().id == id){
                return i;
            }
        }
        return -1;
    }
    
    public int getSelectIndex(){
        return select_idx;
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
        if(select_idx == idx){
            select_idx = -1;
        }
        Item item = items[idx];
        items[idx] = null;
        return item;
    }

    public void remove(Item item){
        for(int i = 0; i < items.length; i++){
            if(items[i] == item){
                items[i] = null;
                if(select_idx == i){
                    select_idx = -1;
                }
            }
        }
    }
    
    public void tick(double timeDelta){
        for(int i = 0; i < items.length; i++){
            Item item = items[i];
            
            if(item != null){
                item.onTick(timeDelta);
                if(item.isTerminated())
                    remove(item);
            }
        }
    }

    public boolean isEmpty() {
        return freeSlotsCount() == size();
    }
}
