package net.dup.finder;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Finder {

    private Multimap<String, Path> values = HashMultimap.create();

    public static void main(String[] args) {
        new Finder().findDuplicates(args[0]);
    }
//C:\Users\Gebruiker\Pictures\test
    private void findDuplicates(String arg) {
        try {
            Files.find(Paths.get(arg),
                    Integer.MAX_VALUE,
                    (filePath, fileAttr) -> fileAttr.isRegularFile())
                    .forEach(getPrintln());
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (String checksumKey : values.keySet()) {
            Collection<Path> pathCollection = values.get(checksumKey);
            if (pathCollection.size() > 1) {
                String join = String.join("\n", pathCollection.stream().map(o -> o.toString()).collect(Collectors.toList()));
                System.out.println(String.format("%d duplicates found:\n%s", pathCollection.size(), join ));
                System.out.println("==========================================================");
            }
        }
    }

    private Consumer<Path> getPrintln() {

        return (Path path) -> {
            byte[] b = new byte[0];
            try {
                b = Files.readAllBytes(path);
            byte[] hash = MessageDigest.getInstance("MD5").digest(b);
                String checkSum = DatatypeConverter.printHexBinary(hash);
                System.out.println(path.getFileName());
                values.put(checkSum, path);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        };
    }
}
