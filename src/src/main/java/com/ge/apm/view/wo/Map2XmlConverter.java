package com.ge.apm.view.wo;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.AbstractCollectionConverter;
import com.thoughtworks.xstream.io.ExtendedHierarchicalStreamWriterHelper;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

public class Map2XmlConverter extends AbstractCollectionConverter
{
	  
	private final Class type;
	  
	  public Map2XmlConverter(Mapper mapper)
	  {
	    this(mapper, null);
	  }
	  
	  public Map2XmlConverter(Mapper mapper, Class type)
	  {
	    super(mapper);
	    this.type = type;
	    if ((type != null) && (!Map.class.isAssignableFrom(type))) {
	      throw new IllegalArgumentException(type + " not of type " + Map.class);
	    }
	  }
	  
	  public boolean canConvert(Class type)
	  {
	    if (this.type != null) {
	      return type.equals(this.type);
	    }
	    return (type.equals(HashMap.class)) || 
	      (type.equals(Hashtable.class)) || 
	      (type.getName().equals("java.util.LinkedHashMap")) || 
	      (type.getName().equals("java.util.concurrent.ConcurrentHashMap")) || 
	      (type.getName().equals("sun.font.AttributeMap") || 
	      (type.equals(HashedMap.class)) );
	  }
	  
	  public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context)
	  {
	    Map map = (Map)source;
	    for (Iterator iterator = map.entrySet().iterator(); iterator.hasNext();)
	    {
	      Map.Entry entry = (Map.Entry)iterator.next();
	      ExtendedHierarchicalStreamWriterHelper.startNode(writer, entry.getKey().toString(), entry.getClass());
	      
	      writeItem(entry.getValue(), context, writer);
	      
	      writer.endNode();
	    }
	  }
	  
	  public void writeItem(Object item, MarshallingContext context, HierarchicalStreamWriter writer)
	  {
	    if (item == null)
	    {
	    	writer.setValue("");
	    }
	    else
	    {
	      context.convertAnother(item);
	    }
	  }
	  
	  public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context)
	  {
	    Map map = (Map)createCollection(context.getRequiredType());
	    populateMap(reader, context, map);
	    return map;
	  }
	  
	  protected void populateMap(HierarchicalStreamReader reader, UnmarshallingContext context, Map map)
	  {
	    populateMap(reader, context, map, map);
	  }
	  
	  protected void populateMap(HierarchicalStreamReader reader, UnmarshallingContext context, Map map, Map target)
	  {
	    while (reader.hasMoreChildren())
	    {
	      reader.moveDown();
	      putCurrentEntryIntoMap(reader, context, map, target);
	      reader.moveUp();
	    }
	  }
	  
	  protected void putCurrentEntryIntoMap(HierarchicalStreamReader reader, UnmarshallingContext context, Map map, Map target)
	  {
	    reader.moveDown();
	    Object key = readItem(reader, context, map);
	    reader.moveUp();
	    
	    reader.moveDown();
	    Object value = readItem(reader, context, map);
	    reader.moveUp();
	    
	    target.put(key, value);
	  }
	  
	  protected Object createCollection(Class type)
	  {
	    return super.createCollection(this.type != null ? this.type : type);
	  }

}
