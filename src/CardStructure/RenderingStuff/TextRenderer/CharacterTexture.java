package CardStructure.RenderingStuff.TextRenderer;

import org.joml.Vector2f;

public class CharacterTexture {
    public int TextureAddress;
    public Vector2f Size;
    public Vector2f Bearing;
    public long Advance;
    public CharacterTexture(int TextureAddress, Vector2f Size, Vector2f Bearing, long Advance){
        this.TextureAddress = TextureAddress;
        this.Size = Size;
        this.Bearing = Bearing;
        this.Advance = Advance;
    }
}
