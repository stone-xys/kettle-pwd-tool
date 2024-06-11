module com.yykj.kepw {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.xml;
    requires org.eclipse.jetty.util;

    opens com.yykj.kepw to javafx.fxml;
    exports com.yykj.kepw;
}