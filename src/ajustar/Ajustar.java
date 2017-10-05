/*
 */
package ajustar;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.SliderBuilder;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import com.jhlabs.image.*;
import javafx.scene.control.Separator;

/**
 * @author Jesús Alberto Salazar Ortega
 *
 * Aplicación que crea una escena, en ella está contenida una imagen, ya
 * incluida en el proyecto, además de tres barras de desplazamiento, cada una
 * modificando una de las propiedades de la imagen, matiz, saturación y brillo.
 *
 */
public class Ajustar extends Application {

    PixelReader pixelReader;
    int width, height;
    WritableImage writableImage;
    PixelWriter pixelWriter;
    ImageView destImageView;
    Slider sliderHue, sliderSaturation, sliderBrightness;
    double adjHue, adjSaturation, adjBrightness;
    ChoiceBox choices;
    String actualDirectory;
    //List options;
    int efectoActual;
    Image image = new Image("300px-PNG_transparency_demonstration_2.png");
    Scene actualScene;
    Stage primary;

    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) throws IOException {

        primary = primaryStage;
        actualDirectory = ".";
        choices = new ChoiceBox(FXCollections.observableArrayList(
                "Sin efecto", 
                "Escala de grises", 
                "Efecto sepia", 
                "Colores invertidos", 
                "Efecto Pixelado", 
                "Efecto Chrome",
                "Detección bordes",
                "Filtro solarizado",
                "Filtro luminoso",
                "Filtro agua"));
        //options = choices.getItems();
        //Creamos un lector de píxeles
        pixelReader = image.getPixelReader();
        width = (int) image.getWidth();
        height = (int) image.getHeight();

        //Creamos la imagen para la escena, copia pixel a pixal desde la fuente, son 4 copias, la original más 3 para ser modificadas.
        writableImage = new WritableImage(width, height);
        pixelWriter = writableImage.getPixelWriter();

        //Añadimos las 4 imagenes a la escena
        destImageView = new ImageView();

        primaryScene();
        primaryStage.setTitle("Trabajo multimedia - Manipulación de imágenes");
        reloadStage();
        primaryStage.centerOnScreen();
        primaryStage.setResizable(false);
        primaryStage.show();
        updateImage();
    }
    
        private void primaryScene() {
        /*
         * Contenedor para la imagen que va a ser mostrada
         */
        HBox hBox_Image = new HBox();
        hBox_Image.getChildren().addAll(destImageView);
        hBox_Image.setAlignment(Pos.CENTER);

        /*
         * Control para la imagen del matiz
         */
        //Creamos la barra de desplazamiento, de -360 a 360, con saltos de 30 en 30 visibles en la barra, valor inicial 0, con anchura de 300 px
        sliderHue = SliderBuilder.create()
                .prefWidth(300)
                .min(-360)
                .max(360)
                .majorTickUnit(30)
                .showTickMarks(true)
                .showTickLabels(true)
                .value(0)
                .build();

        //Añadimos un listener para saber cuando se mueve la barra
        sliderHue.valueProperty().addListener(sliderChangeListener);

        //Ponemos la barra en la escena
        HBox hBox_Hue = new HBox();
        Label hueLabel = new Label("Matiz:         ");
        hueLabel.setMinWidth(Control.USE_PREF_SIZE);
        hBox_Hue.getChildren().add(hueLabel);
        hBox_Hue.getChildren().addAll(sliderHue);
        hBox_Hue.setAlignment(Pos.TOP_CENTER);

        /*
         * Control para la imagen de la saturación
         */
        //Creamos la barra de desplazamiento, de -1 a 1, con saltos de 0.2 en 0.2 visibles en la barra, valor inicial 0, con anchura de 300 px
        sliderSaturation = SliderBuilder.create()
                .prefWidth(300)
                .min(-1)
                .max(1)
                .majorTickUnit(0.2)
                .showTickMarks(true)
                .showTickLabels(true)
                .value(0)
                .build();

        //Añadimos un listener para saber cuando se mueve la barra
        sliderSaturation.valueProperty().addListener(sliderChangeListener);

        //Ponemos la barra en la escena
        HBox hBox_Saturation = new HBox();
        Label SaturationLabel = new Label("Saturación: ");
        SaturationLabel.setMinWidth(Control.USE_PREF_SIZE);
        hBox_Saturation.getChildren().add(SaturationLabel);
        hBox_Saturation.getChildren().addAll(sliderSaturation);
        hBox_Saturation.setAlignment(Pos.TOP_CENTER);

        /*
         * Control para la imagen del brillo
         */
        //Creamos la barra de desplazamiento, de -1 a 1, con saltos de 0.2 en 0.2 visibles en la barra, valor inicial 0, con anchura de 300 px
        sliderBrightness = SliderBuilder.create()
                .prefWidth(300)
                .min(-1)
                .max(1)
                .majorTickUnit(0.2)
                .showTickMarks(true)
                .showTickLabels(true)
                .value(0)
                .build();


        //Añadimos un listener para saber cuando se mueve la barra
        sliderBrightness.valueProperty().addListener(sliderChangeListener);

        //Ponemos la barra en la escena
        HBox hBox_Brightness = new HBox();
        Label BrightnessLabel = new Label("Brillo:          ");
        BrightnessLabel.setMinWidth(Control.USE_PREF_SIZE);
        hBox_Brightness.getChildren().add(BrightnessLabel);
        hBox_Brightness.getChildren().addAll(sliderBrightness);
        hBox_Brightness.setAlignment(Pos.TOP_CENTER);

        //Espaciado inicial
        Label SpaceLabel = new Label("         ");
        HBox hBox_space = new HBox();
        hBox_space.getChildren().add(SpaceLabel);
        hBox_space.setAlignment(Pos.TOP_CENTER);

        //Créditos
        Button credits = new Button();
        credits.setText("Créditos");
        credits.setOnAction(btnCreditsEventListener);

        //Botón para limpiar la escena
        Button clear = new Button();
        clear.setText("Reiniciar");
        clear.setOnAction(btnClearEventListener);

        //Botón para abrir una imagen
        Button openImg = new Button();
        openImg.setText("Abrir Imagen");
        openImg.setOnAction(btnLoadEventListener);

        //Caja común para los botones de Reinicio y abrir archivo
        HBox hBox_clear = new HBox();
        hBox_clear.setMinWidth(Control.USE_PREF_SIZE);
        hBox_clear.setSpacing(35);
        hBox_clear.getChildren().addAll(openImg, credits, clear);
        hBox_clear.setAlignment(Pos.TOP_CENTER);

        //Espaciado inicial
        Label MsgLabel = new Label("Imagen de previsualización");
        HBox hBox_msg = new HBox();
        hBox_msg.getChildren().add(MsgLabel);
        hBox_msg.setAlignment(Pos.TOP_CENTER);

        //Pestaña desplegable para efectos
        choices.getSelectionModel().selectedIndexProperty().addListener(choiceEventListener);
        choices.setTooltip(new Tooltip("Seleccione un efecto"));
        choices.setValue("Sin efecto");

        Label EffectsLabel = new Label("Efectos:    ");
        HBox hBox_effects = new HBox();
        hBox_effects.getChildren().addAll(EffectsLabel, choices);
        hBox_effects.setAlignment(Pos.TOP_CENTER);

        //Espaciado inicial
        Label SpaceLabel1 = new Label("         ");
        HBox hBox_space1 = new HBox();
        hBox_space1.getChildren().add(SpaceLabel1);
        hBox_space1.setAlignment(Pos.TOP_CENTER);

        //Botón para guardar la imagen
        Button saveImg = new Button();
        saveImg.setText("Guardar imagen");
        saveImg.setOnAction(btnSaveEventListener);

        //Botón para salir
        Button exit = new Button();
        exit.setText("Salir");
        exit.setMinWidth(65);
        exit.setOnAction(btnExitEventListener);

        HBox hBox_exit = new HBox();
        hBox_exit.setMinWidth(Control.USE_PREF_SIZE);
        hBox_exit.setSpacing(125);
        hBox_exit.getChildren().addAll(saveImg, exit);
        hBox_exit.setAlignment(Pos.TOP_CENTER);



        //Creamos un contenedor general para la escena y metemos la imagen y las 3 barras dentro.
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.getChildren().addAll(hBox_space, hBox_clear, hBox_msg, hBox_Image, hBox_Hue, hBox_Saturation, hBox_Brightness, hBox_effects, hBox_space1, hBox_exit);

        StackPane root = new StackPane();
        root.getChildren().add(vBox);
        actualScene = new Scene(root, 400, 560);
    }
    
    private void reloadImage(BufferedImage bi) {

        image = SwingFXUtils.toFXImage(bi, null);
        efectoActual = 0;
        //Creamos un lector de píxeles
        pixelReader = image.getPixelReader();
        width = (int) image.getWidth();
        height = (int) image.getHeight();

        //Creamos la imagen para la escena, copia pixel a pixal desde la fuente, son 4 copias, la original más 3 para ser modificadas.
        writableImage = new WritableImage(width, height);
        pixelWriter = writableImage.getPixelWriter();
        updateImage();
    }

    private void reloadStage() {
        primary.setScene(actualScene);
    }

    ChangeListener<Number> sliderChangeListener = new ChangeListener<Number>() {
        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            updateImage();
        }
    };
    ChangeListener<Number> choiceEventListener = new ChangeListener<Number>() {
        @Override
        public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
            efectoActual = t1.intValue();
            updateImage();
        }
    };
    EventHandler<ActionEvent> btnCreditsEventListener = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent t) {
            //Créditos
            Label CreditsLabel = new Label("Créditos");
            Label Name1 = new Label("Jesús Alberto Salazar Ortega");
            Label Name2 = new Label("Creado para clase de multimedia");
            HBox hBox_credit = new HBox();
            hBox_credit.getChildren().addAll(CreditsLabel);
            hBox_credit.setAlignment(Pos.BASELINE_CENTER);

            HBox hBox_name1 = new HBox();
            hBox_name1.getChildren().addAll(Name1);
            hBox_name1.setAlignment(Pos.BASELINE_CENTER);

            HBox hBox_name2 = new HBox();
            hBox_name2.getChildren().addAll(Name2);
            hBox_name2.setAlignment(Pos.BASELINE_CENTER);

            Label spaceLabel = new Label(" ");
            HBox hBox_space = new HBox();
            hBox_space.getChildren().addAll(spaceLabel);
            hBox_space.setAlignment(Pos.BASELINE_CENTER);

            Label spaceLabel2 = new Label(" ");
            HBox hBox_space2 = new HBox();
            hBox_space2.getChildren().addAll(spaceLabel2);
            hBox_space2.setAlignment(Pos.BASELINE_CENTER);

            //Botón para salir
            Button returnBtn = new Button();
            returnBtn.setText("Volver");
            returnBtn.setOnAction(btnReturnEventListener);
            HBox hBox_return = new HBox();
            hBox_return.getChildren().add(returnBtn);
            hBox_return.setAlignment(Pos.BASELINE_CENTER);

            VBox vBoxCredits = new VBox();
            vBoxCredits.setSpacing(10);
            vBoxCredits.getChildren().addAll(hBox_space2, hBox_credit, new Separator(), hBox_name1, hBox_name2, hBox_space, hBox_return);

            StackPane root = new StackPane();
            root.getChildren().add(vBoxCredits);
            actualScene = new Scene(root, 200, 190);
            primary.setTitle("Créditos");
            reloadStage();

        }
    };
    EventHandler<ActionEvent> btnClearEventListener = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent t) {
            System.out.println("Reiniciar valores");
            sliderHue.setValue(0);
            sliderSaturation.setValue(0);
            sliderBrightness.setValue(0);
            efectoActual = 0;
            choices.setValue("Sin efecto");
            updateImage();
        }
    };
    EventHandler<ActionEvent> btnSaveEventListener = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent t) {

            //Creamos selector de apertura
            JFileChooser chooser = new JFileChooser();

            //Titulo que llevara la ventana
            chooser.setDialogTitle("Seleccionar imagen");
            //chooser.setSelectedFile(new File(actualDirectory));
            chooser.setApproveButtonText("Guardar");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            //Filtro
            //FileNameExtensionFilter filtro = new FileNameExtensionFilter("Imágenes", "png", "jpg", "gif", "jpeg");
            //chooser.setFileFilter(filtro);
            //chooser.setAcceptAllFileFilterUsed(false);

            //Si seleccionamos algún archivo retornaremos su directorio
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                File file;

                if (chooser.getSelectedFile().isDirectory()) {
                    file = new File(chooser.getSelectedFile().toString() + File.separator + "Image.png");
                } else {
                    file = new File(chooser.getSelectedFile().toString());
                }

                if (!file.getName().endsWith(".png")) {
                    file = new File(chooser.getSelectedFile().toString() + ".png");
                }

                //actualDirectory = chooser.getSelectedFile().getAbsolutePath();
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);
                
                
                try {
                    ImageIO.write(bufferedImage, "png", file);
                    //reloadImage(bufferedImage);
                    System.out.println("Imagen guardada");
                } catch (IOException ex) {
                    Logger.getLogger(Ajustar.class.getName()).log(Level.SEVERE, null, ex);
                }

            } else {
                //Si no seleccionamos nada retornaremos No seleccion
                System.out.println("No seleccion");
            }



        }
    };
    EventHandler<ActionEvent> btnExitEventListener = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent t) {
            System.exit(0);
        }
    };
    EventHandler<ActionEvent> btnReturnEventListener = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent t) {
            primaryScene();
            reloadStage();
            primary.setTitle("Trabajo multimedia - Manipulación de imágenes");
        }
    };
    EventHandler<ActionEvent> btnLoadEventListener = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent t) {
            //Creamos selector de apertura
            JFileChooser chooser = new JFileChooser();

            //Titulo que llevara la ventana
            chooser.setDialogTitle("Seleccionar imagen");
            chooser.setSelectedFile(new File(actualDirectory));

            //Filtro
            FileNameExtensionFilter filtro = new FileNameExtensionFilter("Imágenes", "png", "jpg", "gif", "jpeg");
            chooser.setFileFilter(filtro);
            chooser.setAcceptAllFileFilterUsed(false);

            //Si seleccionamos algún archivo retornaremos su directorio
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                actualDirectory = chooser.getSelectedFile().getAbsolutePath();
                BufferedImage bufferedImage;
                try {
                    bufferedImage = ImageIO.read(file);
                    reloadImage(bufferedImage);
                    System.out.println("Reiniciar valores");
                    sliderHue.setValue(0);
                    sliderSaturation.setValue(0);
                    sliderBrightness.setValue(0);
                    efectoActual = 0;
                    choices.setValue("Sin efecto");
                } catch (IOException ex) {
                    Logger.getLogger(Ajustar.class.getName()).log(Level.SEVERE, null, ex);
                }

            } else {
                //Si no seleccionamos nada retornaremos No seleccion
                System.out.println("No seleccion");
            }


        }
    };

    private WritableImage sepia() {
        WritableImage wrimage = new WritableImage(width, height);
        PixelWriter pixwriter = wrimage.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = pixelReader.getColor(x, y);
                double red = (color.getRed() * 0.393) + (color.getGreen() * 0.769) + (color.getBlue() * 0.189);
                double green = (color.getRed() * 0.349) + (color.getGreen() * 0.686) + (color.getBlue() * 0.168);
                double blue = (color.getRed() * 0.272) + (color.getGreen() * 0.534) + (color.getBlue() * 0.131);

                if (red > 1) {
                    red = 1;
                }
                if (green > 1) {
                    green = 1;
                }
                if (blue > 1) {
                    blue = 1;
                }

                color = new Color(red, green, blue, color.getOpacity());
                pixwriter.setColor(x, y, color);
            }
        }

        return wrimage;
    }

    private WritableImage greyScale() {
        WritableImage wrimage = new WritableImage(width, height);
        PixelWriter pixwriter = wrimage.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = pixelReader.getColor(x, y);
                color = color.grayscale();
                pixwriter.setColor(x, y, color);
            }
        }

        return wrimage;
    }

    private WritableImage invert() {
        WritableImage wrimage = new WritableImage(width, height);
        PixelWriter pixwriter = wrimage.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = pixelReader.getColor(x, y);
                color = color.invert();
                pixwriter.setColor(x, y, color);
            }
        }

        return wrimage;
    }

    private void updateImage() {
        adjHue = sliderHue.valueProperty().doubleValue();
        adjSaturation = sliderSaturation.valueProperty().doubleValue();
        adjBrightness = sliderBrightness.valueProperty().doubleValue();

        WritableImage wI = writableImage;
        PixelReader pR = image.getPixelReader();

        if (efectoActual == 1) {
            wI = greyScale();
            pR = wI.getPixelReader();
        }

        if (efectoActual == 2) {
            wI = sepia();
            pR = wI.getPixelReader();
        }

        if (efectoActual == 3) {
            wI = invert();
            pR = wI.getPixelReader();
        }

        if (efectoActual == 4) {

            BlockFilter bF = new BlockFilter();
            bF.setBlockSize(5);
            BufferedImage bI = SwingFXUtils.fromFXImage(image, null);
            bI = bF.filter(bI, null);
            wI = SwingFXUtils.toFXImage(bI, null);
            pR = wI.getPixelReader();
        }

        if (efectoActual == 5) {
            ChromeFilter cF = new ChromeFilter();
            BufferedImage bI = SwingFXUtils.fromFXImage(image, null);
            bI = cF.filter(bI, null);
            wI = SwingFXUtils.toFXImage(bI, null);
            pR = wI.getPixelReader();
        }
        
        if (efectoActual == 6) {
            EdgeFilter eF = new EdgeFilter(); 
            BufferedImage bI = SwingFXUtils.fromFXImage(image, null);
            bI = eF.filter(bI, null);
            wI = SwingFXUtils.toFXImage(bI, null);
            pR = wI.getPixelReader();
        }
        if (efectoActual == 7) {
            SolarizeFilter eF = new SolarizeFilter(); 
            BufferedImage bI = SwingFXUtils.fromFXImage(image, null);
            bI = eF.filter(bI, null);
            wI = SwingFXUtils.toFXImage(bI, null);
            pR = wI.getPixelReader();
        }
        if (efectoActual == 8) {
            RaysFilter eF = new RaysFilter(); 
            BufferedImage bI = SwingFXUtils.fromFXImage(image, null);
            bI = eF.filter(bI, null);
            wI = SwingFXUtils.toFXImage(bI, null);
            pR = wI.getPixelReader();
        }
        if (efectoActual == 9) {
            SwimFilter eF = new SwimFilter(); 
            eF.setAmount(10);
            BufferedImage bI = SwingFXUtils.fromFXImage(image, null);
            bI = eF.filter(bI, null);
            wI = SwingFXUtils.toFXImage(bI, null);
            pR = wI.getPixelReader();
        }





        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = pR.getColor(x, y);
                //pixelWriter.setColor(x, y, color);

                double hue = color.getHue() + adjHue;
                if (hue > 360.0) {
                    hue = hue - 360;
                } else if (hue < 0.0) {
                    hue = hue + 360.0;
                }

                double saturation = color.getSaturation() + adjSaturation;
                if (saturation > 1.0) {
                    saturation = 1.0;
                } else if (saturation < 0.0) {
                    saturation = 0.0;
                }

                double brightness = color.getBrightness() + adjBrightness;
                if (brightness > 1.0) {
                    brightness = 1.0;
                } else if (brightness < 0.0) {
                    brightness = 0.0;
                }

                double opacity = color.getOpacity();

                Color newColor = Color.hsb(hue, saturation, brightness, opacity);
                pixelWriter.setColor(x, y, newColor);
            }
        }

        destImageView.setFitHeight(200);
        destImageView.setFitWidth(200 * width / height);
        destImageView.setImage(writableImage);
    }
}