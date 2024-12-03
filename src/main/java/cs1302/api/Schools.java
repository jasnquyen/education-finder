package cs1302.api;

/** Collects data on schools.
 *
 */

public class Schools {
    private final String name;
    private final String address;
    private final double rating;

    /** Method to construct data on schools.
     * @param name
     * @param address
     * @param rating
     */

    public Schools(String name, String address, double rating) {
        this.name = name;
        this.address = address;
        this.rating = rating;
    }

    /** Get name of school.
     * @return String
     */

    public String getName() {
        return name;
    }

    /** Get address of school.
     * @return String
     */

    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return name + " (" + address + ") - Rating: " + rating;
    }
}
