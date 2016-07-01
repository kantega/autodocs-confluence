package org.kantega.documenter;

import fj.data.List;
import fj.data.Validation;
import org.apache.commons.lang3.StringUtils;

public class MavenCoordinates {
    public final String extension;
    public final String classifier;
    public final String resourceName;
    public final String groupId;
    public final String artifactId;
    public final String version;

    public MavenCoordinates(
      String resourceName,
      String groupId,
      String artifactId,
      String version,
      String classifier,
      String extension) {
        this.resourceName = resourceName;
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.classifier = classifier;
        this.extension = extension;
    }

    public static MavenCoordinates coords(
      String groupId,
      String artifactId,
      String version,
      String qualifier,
      String extension) {
        String resourceName = artifactId + "-" + version;

        return new MavenCoordinates(resourceName, groupId, artifactId, version, qualifier, extension);
    }

    public static Validation<String, MavenCoordinates> fromString(String str, String qualifier, String extension) {
        if (StringUtils.isBlank(str)) {
            return Validation.fail("The coordinates are empty");
        }

        String[] parts = StringUtils.split(str, ":");
        if (parts.length != 3) {
            return Validation.fail("The coordinates must be written in the format groupId:artifactId:version");
        }

        return Validation.success(MavenCoordinates.coords(parts[0], parts[1], parts[2], qualifier, extension));
    }

    public List<String> toPath() {
        List<String> groupPath = List.arrayList(StringUtils.split(groupId, "."));
        return groupPath.append(List.single(artifactId)).append(List.single(version)).append(List.single(resourceName()));
    }

    public String resourceName() {
        return StringUtils.isBlank(classifier) ? resourceName + "." + extension : resourceName + "-" + classifier + "." + extension;
    }

    public MavenCoordinates withQualifier(String qualifier) {
        return new MavenCoordinates(resourceName, groupId, artifactId, version, qualifier, extension);
    }

    public String asString(){
        return groupId+":"+artifactId+":"+extension+":"+classifier+":"+version;
    }

    @Override
    public String toString() {
        return "MavenCoordinates{" +
          "extension='" + extension + '\'' +
          ", classifier='" + classifier + '\'' +
          ", resourceName='" + resourceName + '\'' +
          ", groupId='" + groupId + '\'' +
          ", artifactId='" + artifactId + '\'' +
          ", version='" + version + '\'' +
          '}';
    }
}
