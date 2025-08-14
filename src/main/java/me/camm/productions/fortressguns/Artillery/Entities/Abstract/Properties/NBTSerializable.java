package me.camm.productions.fortressguns.Artillery.Entities.Abstract.Properties;

public interface NBTSerializable<T> {

    public void serialize(T t);

    public T deserialize();
}
