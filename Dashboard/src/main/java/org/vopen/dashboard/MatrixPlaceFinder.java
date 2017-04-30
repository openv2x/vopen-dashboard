package org.vopen.dashboard;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by giovanni on 26/09/15.
 */
public class MatrixPlaceFinder
{
    private Matrix matrix;
    private int minWidth, minHeight, minArea;
    private int maxWidth, maxHeight, maxArea;

    private Set<AdvancedMatrixRect> solutions = new HashSet<>();
    private int count = 0;

    public MatrixPlaceFinder(Matrix matrix)
    {
        this.matrix = matrix;
    }

    public void setMinCostraints(int width, int height, int area)
    {
        minWidth = width;
        minHeight = height;
        minArea = area;
    }

    public void setMaxContstraints(int width, int height, int area)
    {
        maxWidth = width;
        maxHeight = height;
        maxArea = area;
    }

    public MatrixRect findPlaceAround(int column, int row)
    {
        AdvancedMatrixRect rect = new AdvancedMatrixRect(column, row, column, row);
        recursive(rect);
        Log.v("recursive", solutions.size() + " " + count);

        if (solutions.size() == 0)
        {
            return null;
        }

        List<AdvancedMatrixRect> solutionsList = new ArrayList<>();
        solutionsList.addAll(solutions);

        Collections.sort(solutionsList, Collections.reverseOrder(new AreaComparator()));

        int biggestArea = solutionsList.get(0).getArea();


        List<AdvancedMatrixRect> biggestAreaSolutionList = new ArrayList<>();

        for (AdvancedMatrixRect solution : solutionsList)
        {
            if (solution.getArea() == biggestArea)
            {
                biggestAreaSolutionList.add(solution);
            }
            else
            {
                break;
            }
        }

        Collections.sort(biggestAreaSolutionList, new DistanceComparator(column, row));

        float smallestDistance = biggestAreaSolutionList.get(0).getSquaredDistanceFrom(column, row);

        List<AdvancedMatrixRect> smallestDistanceSolutionList = new ArrayList<>();

        for (AdvancedMatrixRect solution : biggestAreaSolutionList)
        {
            if (solution.getSquaredDistanceFrom(column, row) == smallestDistance)
            {
                smallestDistanceSolutionList.add(solution);
            }
            else
            {
                break;
            }
        }

        Collections.sort(smallestDistanceSolutionList, new AngleComparator(column, row));

        double smallestAngle = smallestDistanceSolutionList.get(0).getAngleRelativeTo(column, row);

        List<AdvancedMatrixRect> smallestAngleSolutionList = new ArrayList<>();

        for (AdvancedMatrixRect solution : smallestDistanceSolutionList)
        {
            if (solution.getAngleRelativeTo(column, row) == smallestAngle)
            {
                smallestAngleSolutionList.add(solution);
            }
            else
            {
                break;
            }
        }


        return smallestAngleSolutionList.get(0);
    }

    private void recursive(AdvancedMatrixRect rect)
    {
        // Log.v("whoa","" + rect.getArea());
        count++;

        if (rect.width > maxWidth || rect.height > maxHeight || rect.area > maxArea)
        {
            return;
        }

        try
        {
            if (!matrix.isRectFree(rect))
            {
                return;
            }
        }
        catch (Matrix.MatrixException e)
        {
            return;
        }

        if (solutions.contains(rect))
        {
            return;
        }

        if (rect.width >= minWidth && rect.height >= minHeight && rect.area >= minArea)
        {
            solutions.add(rect);
        }


        recursive(rect.newIncreasedRect(IncreaseDirection.N));
        recursive(rect.newIncreasedRect(IncreaseDirection.S));
        recursive(rect.newIncreasedRect(IncreaseDirection.E));
        recursive(rect.newIncreasedRect(IncreaseDirection.W));
    }

    private enum IncreaseDirection
    {
        N, S, W, E,
    }

    private class AdvancedMatrixRect extends MatrixRect
    {
        public int width;
        public int height;
        public int area;


        public AdvancedMatrixRect(int columnStart, int rowStart, int columnEnd, int rowEnd)
        {
            super(columnStart, rowStart, columnEnd, rowEnd);
            width = columnEnd - columnStart + 1;
            height = rowEnd - rowStart + 1;
            area = width * height;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
            {
                return true;
            }
            if ((obj == null) || (obj.getClass() != this.getClass()))
            {
                return false;
            }


            AdvancedMatrixRect advancedMatrixRect = (AdvancedMatrixRect)obj;
            boolean same =
                    columnStart == advancedMatrixRect.columnStart && rowStart == advancedMatrixRect.rowStart &&
                    columnEnd == advancedMatrixRect.columnEnd && rowEnd == advancedMatrixRect.rowEnd;

            return same;
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + columnStart;
            result = prime * result + columnEnd;
            result = prime * result + rowStart;
            result = prime * result + rowEnd;
            return result;
        }

        public int getWidth()
        {
            return width;
        }

        public int getHeight()
        {
            return height;
        }

        public int getArea()
        {
            return area;
        }

        public AdvancedMatrixRect newIncreasedRect(IncreaseDirection direction)
        {

            int newColumnStart = this.columnStart;
            int newRowStart = this.rowStart;
            int newColumnEnd = this.columnEnd;
            int newRowEnd = this.rowEnd;

            switch (direction)
            {
                case N:
                    newRowStart--;
                    break;
                case S:
                    newRowEnd++;
                    break;
                case W:
                    newColumnStart--;
                    break;
                case E:
                    newColumnEnd++;
                    break;
            }

            return new AdvancedMatrixRect(newColumnStart, newRowStart, newColumnEnd, newRowEnd);
        }

        public float getSquaredDistanceFrom(int column, int row)
        {
            float myCenterX = (float)(columnEnd + columnStart + 1) / 2;
            float myCenterY = (float)(rowEnd + rowStart + 1) / 2;

            float targetX = column + 0.5f;
            float targetY = row + 0.5f;

            return (myCenterX - targetX) * (myCenterX - targetX) + (myCenterY - targetY) * (myCenterY - targetY);
        }

        public double getAngleRelativeTo(int column, int row)
        {
            float myCenterX = (float)(columnEnd + columnStart + 1) / 2;
            float myCenterY = (float)(rowEnd + rowStart + 1) / 2;

            float targetX = column + 0.5f;
            float targetY = row + 0.5f;

            double cos = targetX - myCenterX;
            double sin = targetY - myCenterY;

            double angle = Math.atan2(sin, cos);

            angle = (angle > 0 ? angle : (2 * Math.PI + angle)) * 360 / (2 * Math.PI);

            // This is not enough, but the user experience seems ok.
            // The idea is to give the angle relative to the targetX, targetY where the NW is the 0
            // and then goes +/- 180 degrees. But anyway, it's working,
            // whatever.

            return angle;
        }
    }

    private class AreaComparator implements Comparator<AdvancedMatrixRect>
    {
        @Override
        public int compare(AdvancedMatrixRect o1, AdvancedMatrixRect o2)
        {
            Integer area1 = o1.getArea();
            Integer area2 = o2.getArea();
            return area1.compareTo(area2);
        }
    }

    private class DistanceComparator implements Comparator<AdvancedMatrixRect>
    {
        private int column;
        private int row;

        public DistanceComparator(int column, int row)
        {
            this.column = column;
            this.row = row;
        }

        @Override
        public int compare(AdvancedMatrixRect o1, AdvancedMatrixRect o2)
        {
            Float distance1 = o1.getSquaredDistanceFrom(column, row);
            Float distance2 = o2.getSquaredDistanceFrom(column, row);
            return distance1.compareTo(distance2);
        }
    }

    private class AngleComparator implements Comparator<AdvancedMatrixRect>
    {
        private int column;
        private int row;

        public AngleComparator(int column, int row)
        {
            this.column = column;
            this.row = row;
        }

        @Override
        public int compare(AdvancedMatrixRect o1, AdvancedMatrixRect o2)
        {
            Double angle1 = o1.getAngleRelativeTo(column, row);
            Double angle2 = o2.getAngleRelativeTo(column, row);
            return angle1.compareTo(angle2);
        }
    }
}
