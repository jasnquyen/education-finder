package cs1302.api;

/** Collects data on schools.
 *
 */

public class Schools {
    private final String name;
    private final String address;


    /** Method to construct data on schools.
     * @param name
     * @param address
     */

    public Schools(String name, String address) {
        this.name = name;
        this.address = address;

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
        return name + " (" + address + ") ";
    }
}
