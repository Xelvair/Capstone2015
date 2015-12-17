package capstone2015.graphics;

import capstone2015.geom.Vector2i;

public interface PanelInterface {
    public void     set(Vector2i pos, Tile data);
    public void     aset(Vector2i pos, Tile data);
    public void     blit(PanelInterface panel, Vector2i pos);
    public Tile     get(Vector2i pos);
    public Vector2i size();
}
