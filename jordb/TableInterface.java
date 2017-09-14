package jordb;

/**
 *
 * @author Robert Henry
 * @version 1.0 Last Updated: 06/15/1999
 * @see Table
 * 
 */

public interface TableInterface
{
	public boolean moveFirst() throws TableException;
	public boolean moveNext() throws TableException;
	public int columnIndex(String name);
	public Object get(int index) throws TableException;
	public void set(int index,Object data) throws TableException;
	public int columnCount();
	public Class columnType(int index);

}

