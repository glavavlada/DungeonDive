package test.Model.Util;

import main.Model.util.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Point class.
 * Tests all methods and functionality to ensure 100% coverage.
 */
class PointTest {

    private Point point1;
    private Point point2;
    private Point origin;
    private Point negativePoint;

    @BeforeEach
    void setUp() {
        point1 = new Point(3, 4);
        point2 = new Point(6, 8);
        origin = new Point(0, 0);
        negativePoint = new Point(-2, -3);
    }

    @Test
    void constructor_initializesCoordinatesCorrectly() {
        assertEquals(3, point1.getX(), "X coordinate should be initialized correctly.");
        assertEquals(4, point1.getY(), "Y coordinate should be initialized correctly.");

        assertEquals(0, origin.getX(), "Origin X coordinate should be 0.");
        assertEquals(0, origin.getY(), "Origin Y coordinate should be 0.");

        assertEquals(-2, negativePoint.getX(), "Negative X coordinate should be initialized correctly.");
        assertEquals(-3, negativePoint.getY(), "Negative Y coordinate should be initialized correctly.");
    }

    @Test
    void getX_returnsCorrectXCoordinate() {
        assertEquals(3, point1.getX(), "getX should return correct x coordinate.");
        assertEquals(6, point2.getX(), "getX should return correct x coordinate.");
        assertEquals(0, origin.getX(), "getX should return 0 for origin.");
        assertEquals(-2, negativePoint.getX(), "getX should return negative x coordinate.");
    }

    @Test
    void getY_returnsCorrectYCoordinate() {
        assertEquals(4, point1.getY(), "getY should return correct y coordinate.");
        assertEquals(8, point2.getY(), "getY should return correct y coordinate.");
        assertEquals(0, origin.getY(), "getY should return 0 for origin.");
        assertEquals(-3, negativePoint.getY(), "getY should return negative y coordinate.");
    }

    @Test
    void distance_calculatesCorrectEuclideanDistance() {
        // Distance from (3,4) to (6,8) should be 5.0
        // dx = 6-3 = 3, dy = 8-4 = 4, distance = sqrt(3²+4²) = sqrt(9+16) = sqrt(25) = 5
        assertEquals(5.0, point1.distance(point2), 0.001,
                    "Distance between (3,4) and (6,8) should be 5.0.");

        // Distance from origin to (3,4) should be 5.0
        // dx = 3-0 = 3, dy = 4-0 = 4, distance = sqrt(9+16) = 5
        assertEquals(5.0, origin.distance(point1), 0.001,
                    "Distance from origin to (3,4) should be 5.0.");

        // Distance should be symmetric
        assertEquals(point1.distance(point2), point2.distance(point1), 0.001,
                    "Distance should be symmetric.");
    }

    @Test
    void distance_toSamePoint_returnsZero() {
        assertEquals(0.0, point1.distance(point1), 0.001,
                    "Distance to same point should be 0.");
        assertEquals(0.0, origin.distance(origin), 0.001,
                    "Distance from origin to itself should be 0.");
    }

    @Test
    void distance_withNegativeCoordinates() {
        // Distance from (-2,-3) to (0,0) should be sqrt(4+9) = sqrt(13)
        double expectedDistance = Math.sqrt(13);
        assertEquals(expectedDistance, negativePoint.distance(origin), 0.001,
                    "Distance with negative coordinates should be calculated correctly.");

        // Distance from (-2,-3) to (3,4) should be sqrt(25+49) = sqrt(74)
        double expectedDistance2 = Math.sqrt(74);
        assertEquals(expectedDistance2, negativePoint.distance(point1), 0.001,
                    "Distance between negative and positive coordinates should be correct.");
    }

    @Test
    void distance_horizontalAndVerticalLines() {
        Point horizontal1 = new Point(0, 5);
        Point horizontal2 = new Point(10, 5);
        assertEquals(10.0, horizontal1.distance(horizontal2), 0.001,
                    "Horizontal distance should be calculated correctly.");

        Point vertical1 = new Point(5, 0);
        Point vertical2 = new Point(5, 10);
        assertEquals(10.0, vertical1.distance(vertical2), 0.001,
                    "Vertical distance should be calculated correctly.");
    }

    @Test
    void equals_sameCoordinates_returnsTrue() {
        Point sameAsPoint1 = new Point(3, 4);
        assertTrue(point1.equals(sameAsPoint1), "Points with same coordinates should be equal.");

        Point sameAsOrigin = new Point(0, 0);
        assertTrue(origin.equals(sameAsOrigin), "Origins should be equal.");
    }

    @Test
    void equals_differentCoordinates_returnsFalse() {
        assertFalse(point1.equals(point2), "Points with different coordinates should not be equal.");
        assertFalse(origin.equals(point1), "Origin and non-origin points should not be equal.");
        assertFalse(point1.equals(negativePoint), "Positive and negative coordinate points should not be equal.");
    }

    @Test
    void equals_sameObject_returnsTrue() {
        assertTrue(point1.equals(point1), "Point should equal itself.");
        assertTrue(origin.equals(origin), "Origin should equal itself.");
    }

    @Test
    void equals_nullObject_returnsFalse() {
        assertFalse(point1.equals(null), "Point should not equal null.");
        assertFalse(origin.equals(null), "Origin should not equal null.");
    }

    @Test
    void equals_differentClass_returnsFalse() {
        assertFalse(point1.equals("(3,4)"), "Point should not equal String representation.");
        assertFalse(point1.equals(42), "Point should not equal Integer.");
        assertFalse(point1.equals(new Object()), "Point should not equal generic Object.");
    }

    @Test
    void hashCode_sameCoordinates_returnsSameHashCode() {
        Point sameAsPoint1 = new Point(3, 4);
        assertEquals(point1.hashCode(), sameAsPoint1.hashCode(),
                    "Points with same coordinates should have same hash code.");

        Point sameAsOrigin = new Point(0, 0);
        assertEquals(origin.hashCode(), sameAsOrigin.hashCode(),
                    "Origins should have same hash code.");
    }

    @Test
    void hashCode_differentCoordinates_mayReturnDifferentHashCode() {
        // Note: Different objects may have same hash code, but it's unlikely for well-distributed hash function
        int hash1 = point1.hashCode();
        int hash2 = point2.hashCode();
        int hash3 = origin.hashCode();
        int hash4 = negativePoint.hashCode();

        // At least verify they don't throw exceptions
        assertDoesNotThrow(() -> point1.hashCode(), "hashCode should not throw exception.");
        assertDoesNotThrow(() -> point2.hashCode(), "hashCode should not throw exception.");
        assertDoesNotThrow(() -> origin.hashCode(), "hashCode should not throw exception.");
        assertDoesNotThrow(() -> negativePoint.hashCode(), "hashCode should not throw exception.");
    }

    @Test
    void hashCode_isConsistent() {
        int hash1 = point1.hashCode();
        int hash2 = point1.hashCode();
        assertEquals(hash1, hash2, "Hash code should be consistent across multiple calls.");
    }

    @Test
    void toString_returnsCorrectFormat() {
        assertEquals("(3,4)", point1.toString(), "toString should return correct format for point1.");
        assertEquals("(6,8)", point2.toString(), "toString should return correct format for point2.");
        assertEquals("(0,0)", origin.toString(), "toString should return correct format for origin.");
        assertEquals("(-2,-3)", negativePoint.toString(), "toString should return correct format for negative point.");
    }

    @Test
    void toString_handlesLargeNumbers() {
        Point largePoint = new Point(1000, -2000);
        assertEquals("(1000,-2000)", largePoint.toString(),
                    "toString should handle large numbers correctly.");
    }

    @Test
    void toString_handlesZeroCoordinates() {
        Point zeroX = new Point(0, 5);
        Point zeroY = new Point(5, 0);
        assertEquals("(0,5)", zeroX.toString(), "toString should handle zero x coordinate.");
        assertEquals("(5,0)", zeroY.toString(), "toString should handle zero y coordinate.");
    }

    @Test
    void equalsAndHashCodeContract() {
        Point point1Copy = new Point(3, 4);
        Point point1Another = new Point(3, 4);

        // Reflexive: x.equals(x) should return true
        assertTrue(point1.equals(point1), "equals should be reflexive.");

        // Symmetric: x.equals(y) should return true if and only if y.equals(x) returns true
        assertTrue(point1.equals(point1Copy), "equals should be symmetric (1).");
        assertTrue(point1Copy.equals(point1), "equals should be symmetric (2).");

        // Transitive: if x.equals(y) and y.equals(z), then x.equals(z)
        assertTrue(point1.equals(point1Copy), "equals should be transitive (1).");
        assertTrue(point1Copy.equals(point1Another), "equals should be transitive (2).");
        assertTrue(point1.equals(point1Another), "equals should be transitive (3).");

        // Consistent: multiple invocations should return same result
        assertTrue(point1.equals(point1Copy), "equals should be consistent (1).");
        assertTrue(point1.equals(point1Copy), "equals should be consistent (2).");

        // Hash code contract: if two objects are equal, they must have same hash code
        assertEquals(point1.hashCode(), point1Copy.hashCode(),
                    "Equal objects should have equal hash codes.");
    }

    @Test
    void immutability_coordinatesCannotBeChanged() {
        // Verify that Point is immutable by checking that coordinates don't change
        int originalX = point1.getX();
        int originalY = point1.getY();

        // Perform various operations that shouldn't affect the original point
        point1.distance(point2);
        point1.equals(point2);
        point1.hashCode();
        point1.toString();

        assertEquals(originalX, point1.getX(), "X coordinate should remain unchanged.");
        assertEquals(originalY, point1.getY(), "Y coordinate should remain unchanged.");
    }

    @Test
    void extremeValues_handleCorrectly() {
        Point maxPoint = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
        Point minPoint = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);

        assertEquals(Integer.MAX_VALUE, maxPoint.getX(), "Should handle MAX_VALUE for X.");
        assertEquals(Integer.MAX_VALUE, maxPoint.getY(), "Should handle MAX_VALUE for Y.");
        assertEquals(Integer.MIN_VALUE, minPoint.getX(), "Should handle MIN_VALUE for X.");
        assertEquals(Integer.MIN_VALUE, minPoint.getY(), "Should handle MIN_VALUE for Y.");

        // Test toString with extreme values
        assertDoesNotThrow(() -> maxPoint.toString(), "toString should handle extreme values.");
        assertDoesNotThrow(() -> minPoint.toString(), "toString should handle extreme values.");

        // Test equals and hashCode with extreme values
        assertDoesNotThrow(() -> maxPoint.equals(minPoint), "equals should handle extreme values.");
        assertDoesNotThrow(() -> maxPoint.hashCode(), "hashCode should handle extreme values.");
    }
}
