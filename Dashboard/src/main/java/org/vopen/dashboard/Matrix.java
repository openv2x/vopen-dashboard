package org.vopen.dashboard;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by GVERGINE on 9/24/2015.
 */
public class Matrix
{
    private int columns;
    private int rows;
    private Object[][] matrix;
    private Map<Object, MatrixRect> objectsPositions;

    public Matrix(int columns, int rows)
    {
        this.columns = columns;
        this.rows = rows;
        matrix = new Object[columns][rows];
        objectsPositions = new HashMap<>();
    }

    public Map<Object, MatrixRect> getObjectsPositions()
    {
        return objectsPositions;
    }

    public boolean isRectFreeForObject(Object obj, int columnStart, int rowStart, int columnEnd, int rowEnd) throws MatrixException
    {
        validate(columnStart, rowStart, columnEnd, rowEnd);
        int i, j;
        for (i = columnStart; i <= columnEnd; i++)
        {
            for (j = rowStart; j <= rowEnd; j++)
            {
                if (matrix[i][j] != null && matrix[i][j] != obj)
                {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isRectFree(int columnStart, int rowStart, int columnEnd, int rowEnd) throws MatrixException
    {
        validate(columnStart, rowStart, columnEnd, rowEnd);
        int i, j;
        for (i = columnStart; i <= columnEnd; i++)
        {
            for (j = rowStart; j <= rowEnd; j++)
            {
                if (matrix[i][j] != null)
                {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isRectFreeForObject(Object obj, MatrixRect rect) throws MatrixException
    {
        return isRectFreeForObject(obj, rect.columnStart, rect.rowStart, rect.columnEnd, rect.rowEnd);
    }

    public boolean isRectFree(MatrixRect rect) throws MatrixException
    {
        return isRectFree(rect.columnStart, rect.rowStart, rect.columnEnd, rect.rowEnd);
    }

    public void addObject(Object obj, int columnStart, int rowStart, int columnEnd, int rowEnd) throws MatrixException
    {
        if (objectsPositions.containsKey(obj))
        {
            throw new ObjectAlreadyExistsException();
        }
        else if (!isRectFree(columnStart, rowStart, columnEnd, rowEnd))
        {
            throw new SpaceIsNotFreeException();
        }
        else
        {
            int i, j;
            for (i = columnStart; i <= columnEnd; i++)
            {
                for (j = rowStart; j <= rowEnd; j++)
                {
                    // Log.v("Matrix",this.toString());
                    // Log.v("Matrix","added object " + obj.toString()+ " in " + i + " " + j);
                    matrix[i][j] = obj;
                }
            }
            objectsPositions.put(obj, new MatrixRect(columnStart, rowStart, columnEnd, rowEnd));
        }
    }

    public void addObject(Object obj, MatrixRect rect) throws MatrixException
    {
        addObject(obj, rect.columnStart, rect.rowStart, rect.columnEnd, rect.rowEnd);
    }

    public void removeObject(Object obj) throws MatrixException
    {
        if (!objectsPositions.containsKey(obj))
        {
            throw new ObjectNotFoundException();
        }
        else
        {
            MatrixRect objectPosition = objectsPositions.get(obj);
            emptySpace(objectPosition);
            objectsPositions.remove(obj);
        }
    }

    public void removeAllObjects()
    {
        for (Object object : objectsPositions.keySet())
        {
            MatrixRect objectPosition = objectsPositions.get(object);
            try
            {
                emptySpace(objectPosition);
            }
            catch (MatrixException e)
            {
                e.printStackTrace();
            }
            objectsPositions.remove(object);
        }
    }

    public void moveObject(Object obj, MatrixRect destination) throws MatrixException
    {
        validate(destination);
        if (!objectsPositions.containsKey(obj))
        {
            throw new ObjectNotFoundException();
        }
        if (!isRectFreeForObject(obj, destination))
        {
            throw new SpaceIsNotFreeException();
        }
        emptySpace(objectsPositions.get(obj));
        objectsPositions.remove(obj);
        addObject(obj, destination);
    }

    public Object getObjectAt(int column, int row) throws MatrixException
    {
        validate(column, row, column, row);
        Log.v("Matrix", this.toString());
        return matrix[column][row];
    }

    public Object[] getObjectsAt(int row) throws MatrixException
    {
        Object[] list = new Object[columns];
        for (int i = 0; i < columns; i++)
        {
            list[i] = matrix[i][row];
        }

        return list;
    }

    ////////// VALIDATION ///////////
    public void validateBounds(int columnStart, int rowStart, int columnEnd, int rowEnd) throws MatrixException
    {
        if (columnStart < 0 || rowStart < 0 || columnEnd > columns - 1 || rowEnd > rows - 1)
        {
            throw new OutOfMatrixBoundsException();
        }
        if (columnEnd < columnStart || rowEnd < rowStart)
        {
            throw new NegativeRectException();
        }
    }

    public void validateBounds(MatrixRect rect) throws MatrixException
    {
        validateBounds(rect.columnStart, rect.rowStart, rect.columnEnd, rect.rowEnd);
    }

    public void validate(int columnStart, int rowStart, int columnEnd, int rowEnd) throws MatrixException
    {
        validateBounds(columnStart, rowStart, columnEnd, rowEnd);
    }

    public void validate(MatrixRect rect) throws MatrixException
    {
        validateBounds(rect);
    }

    private void emptySpace(int columnStart, int rowStart, int columnEnd, int rowEnd) throws MatrixException
    {
        validate(columnStart, rowStart, columnEnd, rowEnd);
        int i, j;
        for (i = columnStart; i <= columnEnd; i++)
        {
            for (j = rowStart; j <= rowEnd; j++)
            {
                Log.v("Matrix", "emptied " + i + " " + j);
                matrix[i][j] = null;
            }
        }
    }

    private void emptySpace(MatrixRect rect) throws MatrixException
    {
        emptySpace(rect.columnStart, rect.rowStart, rect.columnEnd, rect.rowEnd);
    }
    //////////

    ////////// EXCEPTIONS ///////////
    public class MatrixException extends Exception
    {

    }

    public class ObjectAlreadyExistsException extends MatrixException
    {
    }

    public class ObjectNotFoundException extends MatrixException
    {
    }

    public class SpaceIsNotFreeException extends MatrixException
    {
    }

    public class NegativeRectException extends MatrixException
    {
    }

    public class OutOfMatrixBoundsException extends MatrixException
    {
    }
    //////////
}
