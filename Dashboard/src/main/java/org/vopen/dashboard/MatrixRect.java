package org.vopen.dashboard;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by GVERGINE on 9/24/2015.
 */
public class MatrixRect implements Parcelable
{
    public int columnStart;
    public int rowStart;
    public int columnEnd;
    public int rowEnd;

    public MatrixRect(MatrixRect rect)
    {
        this.columnStart = rect.columnStart;
        this.rowStart = rect.rowStart;
        this.columnEnd = rect.columnEnd;
        this.rowEnd = rect.rowEnd;
    }

    public MatrixRect(int columnStart, int rowStart, int columnEnd, int rowEnd)
    {
        this.columnStart = columnStart;
        this.rowStart = rowStart;
        this.columnEnd = columnEnd;
        this.rowEnd = rowEnd;
    }

    protected MatrixRect(Parcel in)
    {
        columnStart = in.readInt();
        rowStart = in.readInt();
        columnEnd = in.readInt();
        rowEnd = in.readInt();
    }

    public static final Creator<MatrixRect> CREATOR = new Creator<MatrixRect>()
    {
        @Override
        public MatrixRect createFromParcel(Parcel in)
        {
            return new MatrixRect(in);
        }

        @Override
        public MatrixRect[] newArray(int size)
        {
            return new MatrixRect[size];
        }
    };

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(columnStart);
        dest.writeInt(rowStart);
        dest.writeInt(columnEnd);
        dest.writeInt(rowEnd);
    }
}