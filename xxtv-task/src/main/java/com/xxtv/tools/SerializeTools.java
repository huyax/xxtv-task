package com.xxtv.tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

/**
 * 大数据量序列化用fst
 * 小数据量用jdk
 * 
 * @author zyz
 *
 */
public class SerializeTools
{

	/**
	 * 序列化
	 * 
	 * @param obj
	 * @return
	 */
	public static byte[] serialize(Object obj)
	{
		return fstserialize(obj);
	}

	/**
	 * 反序列化
	 * 
	 * @param bytes
	 * @return
	 */
	public static Object deserialize(byte[] bytes)
	{
		return fstdeserialize(bytes);
	}

	public static byte[] fstserialize(Object obj)
	{
		ByteArrayOutputStream out = null;
		FSTObjectOutput fout = null;
		try
		{
			out = new ByteArrayOutputStream();
			fout = new FSTObjectOutput(out);
			fout.writeObject(obj);
			return out.toByteArray();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (fout != null)
			{
				try
				{
					fout.close();
				}
				catch (IOException e)
				{
				}
			}
		}
		return null;
	}

	public static Object fstdeserialize(byte[] bytes)
	{
		if (bytes == null || bytes.length == 0)
			return null;
		FSTObjectInput in = null;
		try
		{
			in = new FSTObjectInput(new ByteArrayInputStream(bytes));
			return in.readObject();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (in != null)
			{
				try
				{
					in.close();
				}
				catch (IOException e)
				{
				}
			}
		}
		return null;
	}

	public static byte[] javaserialize(Object obj)
	{
		ObjectOutputStream oos = null;
		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(obj);
			return baos.toByteArray();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (oos != null)
			{
				try
				{
					oos.close();
				}
				catch (IOException e)
				{
				}
			}
		}
		return null;
	}

	public static Object javadeserialize(byte[] bits)
	{
		if (bits == null || bits.length == 0)
			return null;
		ObjectInputStream ois = null;
		try
		{
			ByteArrayInputStream bais = new ByteArrayInputStream(bits);
			ois = new ObjectInputStream(bais);
			return ois.readObject();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (ois != null)
			{
				try
				{
					ois.close();
				}
				catch (IOException e)
				{
				}
			}
		}
		return null;
	}

}
