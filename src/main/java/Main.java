import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList; // Importa ArrayList
import java.util.Optional; // Importa Optional
import java.util.regex.Pattern; // Importa Pattern
import java.util.stream.Stream;

public class Main extends JFrame {
    private JTextField idField, nombreField, apellidoField, edadField, emailField;
    private JButton loadButton, showButton, createButton, updateButton, deleteButton;
    private JTextArea usersTextArea;
    private final String FILE_NAME = "usuarios.txt";
    private final String ID_FILE = "last_id.txt"; // Archivo para almacenar el último ID
    private static int nextId = 0;

    public Main() {
        setTitle("CRUD de Usuario");
        setSize(800, 600); // Ajustar tamaño para acomodar el área de texto más grande
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Establecer estilo Nimbus
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.getContentPane().setBackground(Color.decode("#F5F5DC"));

        // Leer el último ID utilizado
        try (Stream<String> stream = Files.lines(Paths.get(ID_FILE))) {
            Optional<String> lastIdOpt = stream.findFirst();
            if (lastIdOpt.isPresent()) {
                nextId = Integer.parseInt(lastIdOpt.get());
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo de ID: " + e.getMessage());
        }

        // Crear componentes
        idField = new JTextField(15);
        nombreField = new JTextField(15);
        apellidoField = new JTextField(15);
        edadField = new JTextField(15);
        emailField = new JTextField(15);

        loadButton = new JButton("Cargar Usuario por ID");
        showButton = new JButton("Mostrar Usuarios");
        createButton = new JButton("Crear Nuevo Usuario");
        updateButton = new JButton("Actualizar Usuario");
        deleteButton = new JButton("Eliminar Usuario");

        // Asignar colores pastel a los botones
        loadButton.setBackground(Color.decode("#FFB6C1"));
        showButton.setBackground(Color.decode("#98FB98"));
        createButton.setBackground(Color.decode("#AFEEEE"));
        updateButton.setBackground(Color.decode("#FFE4E1"));
        deleteButton.setBackground(Color.decode("#F08080"));

        // Configurar placeholder para el campo ID
        idField.setText("Buscar por ID");
        idField.setForeground(Color.GRAY);
        idField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (idField.getText().equals("Buscar por ID")) {
                    idField.setText("");
                    idField.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (idField.getText().isEmpty()) {
                    idField.setForeground(Color.GRAY);
                    idField.setText("Buscar por ID");
                }
            }
        });

        // Añadir componentes al frame
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridy++;
        add(new JLabel("ID:"), gbc);
        gbc.gridy++;
        add(idField, gbc);
        gbc.gridy++;
        add(new JLabel("Nombre:"), gbc);
        gbc.gridy++;
        add(nombreField, gbc);
        gbc.gridy++;
        add(new JLabel("Apellido:"), gbc);
        gbc.gridy++;
        add(apellidoField, gbc);
        gbc.gridy++;
        add(new JLabel("Edad:"), gbc);
        gbc.gridy++;
        add(edadField, gbc);
        gbc.gridy++;
        add(new JLabel("Email:"), gbc);
        gbc.gridy++;
        add(emailField, gbc);

        gbc.gridy++;
        add(loadButton, gbc);
        gbc.gridy++;
        add(showButton, gbc);
        gbc.gridy++;
        add(createButton, gbc);
        gbc.gridy++;
        add(updateButton, gbc);
        gbc.gridy++;
        add(deleteButton, gbc);

        // Área de texto para mostrar usuarios
        usersTextArea = new JTextArea();
        usersTextArea.setEditable(false);
        usersTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(usersTextArea);
        gbc.gridy++;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0; // Ajustar peso para hacer el área de texto más alta
        add(scrollPane, gbc);

        // Añadir action listeners
        loadButton.addActionListener(e -> loadUser());
        showButton.addActionListener(e -> showUsers());
        createButton.addActionListener(e -> createUser());
        updateButton.addActionListener(e -> updateUser());
        deleteButton.addActionListener(e -> deleteUser());
    }

    private void loadUser() {
        String id = idField.getText();
        if (id.equals("Buscar por ID") || id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese un ID válido para cargar", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(id)) {
                    idField.setText(parts[0]);
                    nombreField.setText(parts[1]);
                    apellidoField.setText(parts[2]);
                    edadField.setText(parts[3]);
                    emailField.setText(parts[4]);
                    JOptionPane.showMessageDialog(this, "Usuario cargado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
            }
            JOptionPane.showMessageDialog(this, "No se encontró un usuario con ese ID", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al leer el archivo", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showUsers() {
        StringBuilder users = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                users.append(line).append("\n");
            }
            if (users.length() > 0) {
                usersTextArea.setText(users.toString()); // Mostrar en el área de texto
            } else {
                usersTextArea.setText("No hay usuarios para mostrar");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al leer el archivo", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createUser() {
        if (!validateInputs()) {
            return;
        }

        String id = String.valueOf(nextId++);
        String userData = String.join(",", id, nombreField.getText(), apellidoField.getText(), edadField.getText(), emailField.getText());

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            bw.write(userData);
            bw.newLine();
            JOptionPane.showMessageDialog(this, "Usuario creado con ID: " + id, "Éxito", JOptionPane.INFORMATION_MESSAGE);
            clearFields();
            // Actualizar el último ID utilizado
            try (BufferedWriter idWriter = new BufferedWriter(new FileWriter(ID_FILE))) {
                idWriter.write(String.valueOf(nextId));
            } catch (IOException e) {
                System.out.println("Error al escribir el archivo de ID: " + e.getMessage());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al escribir en el archivo", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateUser() {
        String id = idField.getText();
        if (id.equals("Buscar por ID") || id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, primero cargue un usuario para actualizar", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ArrayList<String> lines = new ArrayList<>();
        boolean found = false;

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(id)) {
                    line = String.join(",", id, nombreField.getText(), apellidoField.getText(), edadField.getText(), emailField.getText());
                    found = true;
                }
                lines.add(line);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al leer el archivo", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!found) {
            JOptionPane.showMessageDialog(this, "No se encontró un usuario con ese ID", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
            JOptionPane.showMessageDialog(this, "Usuario actualizado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            clearFields();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al escribir en el archivo", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteUser() {
        String id = idField.getText();
        if (id.equals("Buscar por ID") || id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, primero cargue un usuario para eliminar", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de que desea eliminar este usuario? Esta acción no se puede deshacer.", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        ArrayList<String> lines = new ArrayList<>();
        boolean found = false;

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (!parts[0].equals(id)) {
                    lines.add(line);
                } else {
                    found = true;
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al leer el archivo", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!found) {
            JOptionPane.showMessageDialog(this, "No se encontró un usuario con ese ID", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
            JOptionPane.showMessageDialog(this, "Usuario eliminado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            clearFields();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al escribir en el archivo", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validateInputs() {
        String nombre = nombreField.getText();
        String apellido = apellidoField.getText();
        String edad = edadField.getText();
        String email = emailField.getText();

        // Validación del nombre y apellido para asegurar que solo contengan letras
        if (!nombre.matches("[a-zA-Z]+") || !apellido.matches("[a-zA-Z]+")) {
            JOptionPane.showMessageDialog(this, "El nombre y apellido deben contener solo letras.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validación de la edad para asegurar que sea un número entero positivo
        if (!edad.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "La edad debe ser un número entero positivo.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validación del correo electrónico para asegurar que tenga un formato válido básico
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                            "[a-zA-Z0-9_+&*-]+)*@" +
                            "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                            "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (!pat.matcher(email).matches()) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese una dirección de correo electrónico válida.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void clearFields() {
        idField.setText("");
        nombreField.setText("");
        apellidoField.setText("");
        edadField.setText("");
        emailField.setText("");
        usersTextArea.setText(""); // Limpiar también el área de texto para mostrar usuarios
        }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main main = new Main();
            main.setVisible(true);
        });
    }
}