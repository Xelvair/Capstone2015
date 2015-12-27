package capstone2015.entity;

public class EntityProto {
    public int id;
    public EntityBaseProto entityBaseProto;
    public MapEntityProto mapEntityProto;
    public ItemProto itemProto;
    public ActorProto actorProto;
    public TileProto tileProto;

    public EntityProto(int id){
        this.id = id;
    }
}
