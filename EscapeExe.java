import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
import java.util.Locale;

public class EscapeExe extends JFrame {

    private static final Color BG = new Color(5, 10, 15);
    private static final Color PANEL = new Color(12, 20, 28);
    private static final Color GREEN = new Color(35, 235, 150);
    private static final Color YELLOW = new Color(255, 194, 75);
    private static final Color WHITE = new Color(224, 234, 242);
    private static final Color MUTED = new Color(145, 164, 180);
    private static final Color RED = new Color(255, 100, 105);

    private final CaseData[] cases = {
        new CaseData(
            "THE SEALED GALLERY",
            "A priceless painting has disappeared from a locked gallery. " +
            "The CCTV shows nobody entering or leaving. The frame is still " +
            "on the wall, but the dust pattern beneath it has changed.",
            new String[] {
                "CCTV: Nobody used the gallery door.",
                "FRAME: Fresh tool marks around the frame.",
                "WALL: A narrow strip behind it is dust-free."
            },
            "Connect the CCTV, frame marks and dust pattern. " +
            "How could the painting be removed without using the door?",
            "Think about what could be hidden behind a wall frame.",
            new String[] {"hidden passage", "secret passage", "wall passage", "hidden wall"},
            "A HIDDEN PASSAGE was used behind the frame."
        ),
        new CaseData(
            "THE MIDNIGHT LOGIN",
            "A school server received an unusual command at 9:14 PM. " +
            "The employee's account had been disabled at 9:00 PM, " +
            "but an earlier login session was still active.",
            new String[] {
                "ACCOUNT: Disabled at 9:00 PM.",
                "SESSION: An older login remained active.",
                "LOG: Unknown command received at 9:14 PM."
            },
            "The account was disabled before the command arrived. " +
            "What could still allow the older session to work?",
            "A login can leave behind a temporary digital credential.",
            new String[] {  "old session token", " old valid session token"},
            "An ACTIVE SESSION TOKEN allowed the command."
        ),
        new CaseData(
            "THE MISSING DIAMOND",
            "A diamond vanished from a sealed display case. The case's " +
            "seal is intact, and the display weight has not changed. " +
            "A staff record mentions a second stone of matching weight.",
            new String[] {
    "SEAL: No sign the main case was opened.",
    "WEIGHT: Display weight is unchanged.",
    "RECORD: A matching-weight replica was prepared.",
    "PANEL: A hidden maintenance panel was opened before the diamond disappeared."
},
            "Compare the seal, the unchanged weight, and the staff record. " +
            "Which explanation fits all the evidence?.",
            "The weight can stay the same even if the object is different.",
            new String[] {"replica swap", "stone swap", "diamond replaced", "replica replacement", "fake diamond"},
            "The diamond was replaced in a REPLICA SWAP."
        ),
        new CaseData(
    "THE IMPOSSIBLE FERRY",
    "A ferry's departure record says it left at 8:19 PM. " +
    "The engine log confirms the engine started at 8:23 PM. " +
    "A verified harbour sensor records the ferry still docked at 8:21 PM. " +
    "Investigators suspect the departure time was entered incorrectly.",
    new String[] {
        "CAPTAIN'S LOG: Departure recorded at 8:19 PM.",
        "ENGINE LOG: Engine started at 8:23 PM.",
        "HARBOUR SENSOR: Ferry was still docked at 8:21 PM.",
        "RECORD RULE: The ferry cannot depart before its engine starts."
    },
    "Compare the captain's departure time with the engine log. " +
    "Use the harbour sensor to check the timeline. Which recorded " +
    "departure time is impossible?",
    "The captain's time is earlier than the verified engine start. " +
    "Check the difference between those two times.",
    new String[] {
        "captains departure time is false",
        "false captain departure time",
        "incorrect departure time",
        "departure time is wrong",
        "8:19 pm is false",
        
    },
    "The captain's 8:19 PM departure time is false; the engine started at 8:23 PM."
),
        new CaseData(
            "THE LAB MYSTERY",
            "A sealed laboratory cabinet contains a missing sample. " +
            "No staff badge was used to open the cabinet. Investigators " +
            "find a disturbed air filter and a trace of the sample nearby.",
            new String[] {
                "CABINET: Seal appears intact.",
                "ACCESS: No badge entry recorded.",
                "VENT: Filter disturbed; sample trace nearby."
            },
            "Connect the intact seal, missing access record and disturbed " +
            "filter. Which part of the room should investigators inspect?",
            "Look at the route air takes in and out of the laboratory.",
            new String[] {"ventilation duct", "air duct", "vent duct", "air vent", "ventilation vent"},
            "The likely route is the VENTILATION DUCT."
        ),
        new CaseData(
            "THE CLONED BADGE",
            "A secure room records badge K-17 entering at 2:06 PM. " +
            "The badge's owner was seen at a different building at the " +
            "same time. The system checks badge numbers, not faces.",
            new String[] {
                "ENTRY LOG: Badge K-17 used at 2:06 PM.",
                "WITNESS: Its owner was elsewhere.",
                "SYSTEM: Checks badge number, not the person."
            },
            "If the real owner was elsewhere, how could the same badge " +
            "number appear at the secure room?",
            "The system trusts the identifier, even when someone copies it.",
            new String[] {"cloned badge", "copied badge", "duplicate badge", "badge clone", "duplicated badge"},
            "Someone used a CLONED BADGE."
        ),
        new CaseData(
            "THE FALSE ALARM",
            "An evacuation alarm sends everyone outside a research building. " +
            "There is no smoke, and the fire sensors show normal readings. " +
            "A restricted door opened three minutes before the alarm.",
            new String[] {
                "FIRE SENSORS: All readings normal.",
                "DOOR LOG: Restricted door opened first.",
                "TIMING: Alarm sounded three minutes later."
            },
            "Use the sensor readings and the order of events. What is the " +
            "most likely explanation for the alarm?",
            "Ask whether the alarm was a real emergency or a distraction.",
            new String[] {"staged evacuation", "false alarm", "fake evacuation", "deliberate alarm", "planned evacuation"},
            "The alarm was used for a STAGED EVACUATION."
        )
    };

    private final boolean[] solved = new boolean[7];
    private int score = 0;
    private int level = 0;
    private boolean hintUsed = false;

    private final File saveFile = new File(
        System.getProperty("user.home"), ".escapeexe_progress.dat"
    );

    private JPanel root;
    private JTextField answerField;
    private JLabel statusLabel;
    private JLabel scoreLabel;
    private JButton hintButton;
    private JButton verifyButton;

    public EscapeExe() {
        loadProgress();

        setTitle("ESCAPE.exe // Detective Protocol");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(760, 650));
        setSize(1000, 800);
        setLocationRelativeTo(null);
        showHome();
    }

    private void showHome() {
        root = new JPanel(new BorderLayout(0, 18));
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(30, 35, 30, 35));
        setContentPane(root);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel logo = label("ESCAPE.exe", 34, GREEN, true);
        JLabel tag = label("DETECTIVE PROTOCOL  //  7 CASES", 13, YELLOW, true);
        top.add(logo, BorderLayout.NORTH);
        top.add(tag, BorderLayout.SOUTH);
        root.add(top, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        ScenePanel preview = new ScenePanel(0);
        preview.setPreferredSize(new Dimension(650, 300));
        preview.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        preview.setBorder(new LineBorder(GREEN, 1));
        center.add(preview);
        center.add(Box.createVerticalStrut(22));

        JLabel heading = label("YOUR NEXT CASE AWAITS", 24, WHITE, true);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        center.add(heading);
        center.add(Box.createVerticalStrut(10));

        int completed = countSolved();
        JLabel progress = label(
            "CASES SOLVED: " + completed + " / 7     SCORE: " + score,
            16, YELLOW, true
        );
        progress.setAlignmentX(Component.LEFT_ALIGNMENT);
        center.add(progress);
        center.add(Box.createVerticalStrut(12));

        JLabel desc = label(
            "<html>Inspect the scene. Read the clues. Solve the mystery.<br>" +
            "No random guessing needed — every case gives you useful evidence.</html>",
            15, WHITE, false
        );
        desc.setAlignmentX(Component.LEFT_ALIGNMENT);
        center.add(desc);
        center.add(Box.createVerticalStrut(24));

        JButton start = button(
            completed == 7 ? "PLAY AGAIN" : "START GAME",
            GREEN, BG
        );
        start.setAlignmentX(Component.LEFT_ALIGNMENT);
        start.setMaximumSize(new Dimension(260, 52));
        start.addActionListener(e -> {
            if (countSolved() == 7) {
                JOptionPane.showMessageDialog(
                    this, "You solved all seven cases! Your score is " + score + "."
                );
                return;
            }
            level = firstUnsolved();
            showGame();
        });
        center.add(start);
        JButton restart = button("RESTART GAME", YELLOW, BG);
restart.setAlignmentX(Component.LEFT_ALIGNMENT);
restart.setMaximumSize(new Dimension(260, 52));

restart.addActionListener(e -> {
    int choice = JOptionPane.showConfirmDialog(
        this,
        "Restart from Level 1? Your current score and progress will reset.",
        "Restart Game",
        JOptionPane.YES_NO_OPTION
    );

    if (choice == JOptionPane.YES_OPTION) {
        resetGame();
        showGame();
    }
});

center.add(Box.createVerticalStrut(10));
center.add(restart);

        root.add(center, BorderLayout.CENTER);

        JLabel footer = label(
            "BLACK FILES  //  EVIDENCE FIRST, CONCLUSIONS SECOND",
            12, MUTED, true
        );
        root.add(footer, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    private void showGame() {
        if (level < 0 || level >= cases.length) {
            showHome();
            return;
        }

        hintUsed = false;
        CaseData c = cases[level];

        root = new JPanel(new BorderLayout(0, 12));
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(18, 24, 18, 24));
        setContentPane(root);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel logo = label("ESCAPE.exe", 25, GREEN, true);
        scoreLabel = label(
            "CASE " + String.format("%02d", level + 1) + " / 07   //   SCORE: " + score,
            14, YELLOW, true
        );
        header.add(logo, BorderLayout.WEST);
        header.add(scoreLabel, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setBackground(BG);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        ScenePanel scene = new ScenePanel(level);
        scene.setPreferredSize(new Dimension(800, 255));
        scene.setMinimumSize(new Dimension(300, 200));
        scene.setMaximumSize(new Dimension(Integer.MAX_VALUE, 255));
        scene.setBorder(new LineBorder(GREEN, 1));
        scene.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(scene);
        content.add(Box.createVerticalStrut(12));

        JLabel caseTitle = label(c.title, 23, GREEN, true);
        caseTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(caseTitle);
        content.add(Box.createVerticalStrut(7));

        JPanel storyPanel = panel();
        storyPanel.setLayout(new BorderLayout(0, 5));
        storyPanel.add(label("THE MYSTERY", 14, YELLOW, true), BorderLayout.NORTH);
        storyPanel.add(wrap(c.story, 14, WHITE), BorderLayout.CENTER);
        storyPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(storyPanel);
        content.add(Box.createVerticalStrut(8));

        JPanel cluePanel = panel();
        cluePanel.setLayout(new BorderLayout(0, 5));
        cluePanel.add(label("EVIDENCE / CLUES", 14, GREEN, true), BorderLayout.NORTH);

        JPanel clues = new JPanel();
        clues.setOpaque(false);
        clues.setLayout(new BoxLayout(clues, BoxLayout.Y_AXIS));
        for (String clue : c.clues) {
            JLabel item = wrap("• " + clue, 13, WHITE);
            item.setBorder(new EmptyBorder(2, 0, 2, 0));
            clues.add(item);
        }
        cluePanel.add(clues, BorderLayout.CENTER);
        cluePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(cluePanel);
        content.add(Box.createVerticalStrut(8));

        JPanel instructions = panel();
        instructions.setLayout(new BorderLayout(0, 5));
        instructions.add(label("HOW TO SOLVE", 14, GREEN, true), BorderLayout.NORTH);
        instructions.add(wrap(c.instructions, 14, WHITE), BorderLayout.CENTER);
        instructions.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(instructions);
        content.add(Box.createVerticalStrut(10));

        JLabel deduction = label("YOUR DEDUCTION", 14, YELLOW, true);
        deduction.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(deduction);
        content.add(Box.createVerticalStrut(5));

        answerField = new JTextField();
        answerField.setFont(new Font("Consolas", Font.PLAIN, 16));
        answerField.setForeground(WHITE);
        answerField.setCaretColor(GREEN);
        answerField.setBackground(new Color(17, 27, 37));
        answerField.setBorder(new CompoundBorder(
            new LineBorder(GREEN, 1),
            new EmptyBorder(10, 12, 10, 12)
        ));
        answerField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        answerField.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(answerField);
        content.add(Box.createVerticalStrut(8));

        JPanel actions = new JPanel(new GridLayout(1, 3, 10, 0));
        actions.setOpaque(false);
        actions.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        actions.setAlignmentX(Component.LEFT_ALIGNMENT);

        hintButton = button("GET HINT", YELLOW, BG);
        verifyButton = button("VERIFY", GREEN, BG);
        JButton homeButton = button("HOME", MUTED, BG);
        JButton restartButton = button("RESTART", YELLOW, BG);

restartButton.addActionListener(e -> {
    int choice = JOptionPane.showConfirmDialog(
        this,
        "Restart from Level 1? Your score and progress will reset.",
        "Restart Game",
        JOptionPane.YES_NO_OPTION
    );

    if (choice == JOptionPane.YES_OPTION) {
        resetGame();
        showGame();
    }
});

        hintButton.addActionListener(e -> showHint());
        verifyButton.addActionListener(e -> verifyAnswer());
        homeButton.addActionListener(e -> showHome());

        actions.add(hintButton);
        actions.add(verifyButton);
        actions.add(restartButton);
        actions.add(homeButton);
        content.add(actions);
        content.add(Box.createVerticalStrut(6));

        statusLabel = label("Investigation active. Read all clues before answering.", 13, MUTED, false);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(statusLabel);

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.setBackground(BG);
        scroll.getViewport().setBackground(BG);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        root.add(scroll, BorderLayout.CENTER);

        revalidate();
        repaint();
        answerField.requestFocusInWindow();
        answerField.addActionListener(e -> verifyAnswer());
    }

    private void showHint() {
        if (hintUsed) {
            statusLabel.setText("Hint already used. Re-read the evidence and try again.");
            statusLabel.setForeground(YELLOW);
            return;
        }
        hintUsed = true;
        statusLabel.setText("HINT: " + cases[level].hint);
        statusLabel.setForeground(YELLOW);
        hintButton.setEnabled(false);
    }

    private void verifyAnswer() {
        String answer = normalize(answerField.getText());

        if (answer.isEmpty()) {
            statusLabel.setText("Type your conclusion first.");
            statusLabel.setForeground(YELLOW);
            return;
        }

        if (solved[level]) {
            statusLabel.setText("This case is already solved. Return HOME to continue.");
            statusLabel.setForeground(GREEN);
            return;
        }

        for (String accepted : cases[level].answers) {
            if (answer.equals(normalize(accepted))) {
                solved[level] = true;
                score +=100;
                saveProgress();

                statusLabel.setText("CASE SOLVED! " + cases[level].solution);
                statusLabel.setForeground(GREEN);
                scoreLabel.setText(
                    "CASE " + String.format("%02d", level + 1) +
                    " / 07   //   SCORE: " + score
                );

                verifyButton.setEnabled(false);
                hintButton.setEnabled(false);

                if (countSolved() == 7) {
                    JOptionPane.showMessageDialog(
                        this,
                        "ALL CASES SOLVED!\nFinal score: " + score +
                        "\nYou are a top detective.",
                        "ESCAPE.exe COMPLETE",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    SwingUtilities.invokeLater(this::showHome);
                } else {
                    verifyButton.setText("CASE SOLVED");
                    JButton next = button("NEXT CASE", GREEN, BG);
                    next.addActionListener(e -> {
                        level = firstUnsolved();
                        showGame();
                    });

                    JPanel parent = (JPanel) verifyButton.getParent();
                    parent.add(next);
                    parent.revalidate();
                    parent.repaint();
                }
                return;
            }
        }

        statusLabel.setText("Not quite. Recheck the clues and try another conclusion.");
        statusLabel.setForeground(RED);
    }

    private JPanel panel() {
        JPanel p = new JPanel();
        p.setBackground(PANEL);
        p.setBorder(new CompoundBorder(
            new LineBorder(new Color(35, 65, 68), 1),
            new EmptyBorder(9, 11, 9, 11)
        ));
        return p;
    }

    private JLabel label(String text, int size, Color color, boolean bold) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Consolas", bold ? Font.BOLD : Font.PLAIN, size));
        l.setForeground(color);
        return l;
    }

    private JLabel wrap(String text, int size, Color color) {
        JLabel l = new JLabel("<html><div style='width:680px;'>" +
            escapeHtml(text) + "</div></html>");
        l.setFont(new Font("Consolas", Font.PLAIN, size));
        l.setForeground(color);
        return l;
    }

    private String escapeHtml(String s) {
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    private JButton button(String text, Color borderColor, Color textColor) {
        JButton b = new JButton(text);
        b.setFont(new Font("Consolas", Font.BOLD, 14));
        b.setForeground(textColor);
        b.setBackground(BG);
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setBorder(new LineBorder(borderColor, 2));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private String normalize(String s) {
        return s.toLowerCase(Locale.ROOT)
                .trim()
                .replaceAll("[^a-z0-9 ]", "")
                .replaceAll("\\s+", " ");
    }

    private int countSolved() {
        int n = 0;
        for (boolean b : solved) if (b) n++;
        return n;
    }

    private int firstUnsolved() {
        for (int i = 0; i < solved.length; i++) {
            if (!solved[i]) return i;
        }
        return 0;
    }

    private void saveProgress() {
        try (PrintWriter out = new PrintWriter(new FileWriter(saveFile))) {
            out.println(score);
            for (boolean b : solved) out.println(b);
        } catch (IOException ex) {
            System.err.println("Progress could not be saved: " + ex.getMessage());
        }
    }

    private void resetGame() {
    score = 0;
    level = 0;
    hintUsed = false;

    for (int i = 0; i < solved.length; i++) {
        solved[i] = false;
    }

    if (saveFile.exists()) {
        saveFile.delete();
    }
}

    private void loadProgress() {
        if (!saveFile.exists()) return;
        try (BufferedReader in = new BufferedReader(new FileReader(saveFile))) {
            String line = in.readLine();
            if (line != null) score = Math.max(0, Integer.parseInt(line.trim()));
            for (int i = 0; i < solved.length; i++) {
                line = in.readLine();
                if (line != null) solved[i] = Boolean.parseBoolean(line.trim());
            }
        } catch (Exception ex) {
            score = 0;
            for (int i = 0; i < solved.length; i++) solved[i] = false;
        }
    }

    private static class CaseData {
        String title, story, instructions, hint, solution;
        String[] clues, answers;

        CaseData(String title, String story, String[] clues,
                 String instructions, String hint, String[] answers,
                 String solution) {
            this.title = title;
            this.story = story;
            this.clues = clues;
            this.instructions = instructions;
            this.hint = hint;
            this.answers = answers;
            this.solution = solution;
        }
    }

    // Every level has its own illustrated scene drawn directly in Java.
    private class ScenePanel extends JPanel {
        private final int scene;

        ScenePanel(int scene) {
            this.scene = scene;
            setBackground(new Color(7, 15, 23));
            setPreferredSize(new Dimension(700, 250));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            g2.setColor(new Color(8, 17, 26));
            g2.fillRect(0, 0, w, h);

            // Subtle investigation grid
            g2.setColor(new Color(20, 39, 48));
            for (int x = 0; x < w; x += 28) g2.drawLine(x, 0, x, h);
            for (int y = 0; y < h; y += 28) g2.drawLine(0, y, w, y);

            switch (scene) {
                case 0: gallery(g2, w, h); break;
                case 1: server(g2, w, h); break;
                case 2: diamond(g2, w, h); break;
                case 3: ferry(g2, w, h); break;
                case 4: lab(g2, w, h); break;
                case 5: badge(g2, w, h); break;
                case 6: alarm(g2, w, h); break;
            }

            g2.setColor(GREEN);
            g2.setStroke(new BasicStroke(2));
            g2.drawRect(1, 1, w - 3, h - 3);
            g2.dispose();
        }

        private void txt(Graphics2D g, String s, int x, int y, Color c, int size) {
            g.setFont(new Font("Consolas", Font.BOLD, size));
            g.setColor(c);
            g.drawString(s, x, y);
        }

        private void gallery(Graphics2D g, int w, int h) {
            g.setColor(new Color(49, 30, 43));
            g.fillRect(0, 0, w, h * 2 / 3);
            g.setColor(new Color(35, 31, 37));
            g.fillRect(0, h * 2 / 3, w, h / 3);

            int x = w / 2 - 65, y = 32;
            g.setColor(YELLOW);
            g.setStroke(new BasicStroke(6));
            g.drawRect(x, y, 130, 120);
            g.setColor(new Color(16, 24, 34));
            g.fillRect(x + 9, y + 9, 112, 102);
            g.setColor(new Color(55, 83, 95));
            g.drawRect(x + 15, y + 15, 100, 90);
            g.setColor(new Color(50, 58, 64));
            g.fillRect(x + 12, y + 126, 108, 8);

            g.setColor(new Color(80, 80, 75));
            g.fillRect(28, h - 45, 175, 20);
            g.setColor(YELLOW);
            g.drawOval(60, h - 67, 43, 20);
            g.drawLine(78, h - 67, 90, h - 45);

            txt(g, "CCTV: NO ENTRY", 20, 25, GREEN, 13);
            txt(g, "FRESH FRAME MARKS", w - 215, 42, YELLOW, 12);
            txt(g, "DUST-FREE STRIP", w - 215, 62, GREEN, 12);
            txt(g, "INSPECT THE FRAME", 20, h - 15, WHITE, 12);
        }

        private void server(Graphics2D g, int w, int h) {
            for (int i = 0; i < 4; i++) {
                int x = 30 + i * (w / 4);
                g.setColor(new Color(22, 35, 45));
                g.fillRoundRect(x, 25, w / 4 - 28, h - 55, 10, 10);
                g.setColor(GREEN);
                g.drawRoundRect(x, 25, w / 4 - 28, h - 55, 10, 10);
                for (int j = 0; j < 5; j++) {
                    g.setColor(j % 2 == 0 ? GREEN : YELLOW);
                    g.fillOval(x + 12, 45 + j * 28, 7, 7);
                    g.setColor(new Color(50, 80, 90));
                    g.fillRect(x + 27, 46 + j * 28, 42, 4);
                }
            }
            txt(g, "SERVER ACCESS LOG", 20, h - 12, WHITE, 12);
            txt(g, "21:00 ACCOUNT DISABLED", 20, 18, YELLOW, 12);
            txt(g, "21:14 UNKNOWN COMMAND", w - 260, 18, RED, 12);
        }

        private void diamond(Graphics2D g, int w, int h) {
            g.setColor(new Color(35, 41, 49));
            g.fillRect(0, h - 65, w, 65);
            g.setColor(YELLOW);
            g.setStroke(new BasicStroke(4));
            g.drawRoundRect(w / 2 - 100, 30, 200, h - 95, 10, 10);
            g.setColor(new Color(18, 39, 53, 220));
            g.fillRoundRect(w / 2 - 93, 37, 186, h - 109, 8, 8);

            int cx = w / 2, cy = h / 2;
            Polygon gem = new Polygon(
                new int[]{cx - 34, cx - 20, cx + 20, cx + 34, cx},
                new int[]{cy - 8, cy - 27, cy - 27, cy - 8, cy + 30}, 5
            );
            g.setColor(new Color(75, 211, 231));
            g.fillPolygon(gem);
            g.setColor(WHITE);
            g.drawPolygon(gem);
            txt(g, "SEALED CASE", 20, 25, GREEN, 13);
            txt(g, "WEIGHT UNCHANGED", w - 200, 25, YELLOW, 12);
            txt(g, "MATCHING STONE FOUND", 20, h - 15, WHITE, 12);
        }

        private void ferry(Graphics2D g, int w, int h) {
            g.setColor(new Color(10, 22, 39));
            g.fillRect(0, 0, w, h * 2 / 3);
            g.setColor(new Color(22, 49, 61));
            g.fillRect(0, h * 2 / 3, w, h / 3);

            g.setColor(new Color(45, 52, 59));
            g.fillRect(20, h - 60, 150, 20);
            g.setColor(new Color(68, 80, 87));
            g.fillRect(w / 3, h / 2, w / 3, 45);
            g.setColor(WHITE);
            g.fillRect(w / 2 - 55, h / 2 - 28, 110, 28);
            g.setColor(GREEN);
            g.drawLine(w / 2 - 20, h / 2 - 28, w / 2 - 20, h / 2 - 48);
            g.drawLine(w / 2 + 12, h / 2 - 28, w / 2 + 12, h / 2 - 48);
            txt(g, "MANIFEST 20:12", 20, 25, YELLOW, 12);
            txt(g, "CCTV 20:16 / STILL DOCKED", 20, 45, GREEN, 12);
            txt(g, "LOG: DEPARTURE 20:19", w - 250, 25, RED, 12);
        }

        private void lab(Graphics2D g, int w, int h) {
            g.setColor(new Color(17, 34, 42));
            g.fillRect(0, 0, w, h);
            g.setColor(new Color(60, 79, 88));
            g.fillRoundRect(40, 45, 150, 120, 8, 8);
            g.setColor(YELLOW);
            g.drawRoundRect(40, 45, 150, 120, 8, 8);
            g.setColor(new Color(35, 60, 70));
            g.fillRect(55, 65, 120, 80);

            g.setColor(new Color(65, 85, 95));
            g.fillRect(w - 180, 45, 130, 35);
            g.fillRect(w - 180, 90, 130, 35);
            g.setColor(GREEN);
            for (int i = 0; i < 5; i++) {
                g.drawLine(w - 172 + i * 24, 48, w - 172 + i * 24, 77);
                g.drawLine(w - 172 + i * 24, 93, w - 172 + i * 24, 122);
            }
            g.setColor(new Color(45, 65, 75));
            g.fillRect(0, h - 45, w, 45);
            txt(g, "CABINET SEALED", 20, 25, YELLOW, 12);
            txt(g, "FILTER DISTURBED", w - 205, 25, GREEN, 12);
            txt(g, "CHECK THE AIR ROUTE", 20, h - 15, WHITE, 12);
        }

        private void badge(Graphics2D g, int w, int h) {
            g.setColor(new Color(13, 27, 38));
            g.fillRect(0, 0, w, h);
            g.setColor(new Color(28, 45, 57));
            g.fillRoundRect(w / 2 - 65, 28, 130, h - 58, 12, 12);
            g.setColor(GREEN);
            g.setStroke(new BasicStroke(3));
            g.drawRoundRect(w / 2 - 65, 28, 130, h - 58, 12, 12);
            g.setColor(YELLOW);
            g.fillRoundRect(w / 2 - 42, 55, 84, 110, 8, 8);
            g.setColor(BG);
            g.setFont(new Font("Consolas", Font.BOLD, 22));
            g.drawString("K-17", w / 2 - 32, 115);
            g.setColor(GREEN);
            g.drawOval(w / 2 - 10, 38, 20, 20);
            txt(g, "ACCESS LOG: K-17", 20, 25, GREEN, 12);
            txt(g, "OWNER ELSEWHERE", w - 200, 25, YELLOW, 12);
            txt(g, "IDENTIFIER ≠ PERSON", 20, h - 15, WHITE, 12);
        }

        private void alarm(Graphics2D g, int w, int h) {
            g.setColor(new Color(24, 34, 43));
            g.fillRect(0, 0, w, h);
            g.setColor(new Color(53, 63, 70));
            g.fillRect(w / 2 - 70, 40, 140, h - 40);
            g.setColor(YELLOW);
            g.drawRect(w / 2 - 70, 40, 140, h - 40);
            g.setColor(new Color(18, 29, 39));
            g.fillRect(w / 2 - 54, 55, 108, h - 55);
            g.setColor(GREEN);
            g.drawRect(w / 2 - 54, 55, 108, h - 55);

            g.setColor(RED);
            g.fillOval(w / 2 - 17, 8, 34, 34);
            g.setColor(new Color(255, 100, 105, 75));
            g.fillOval(w / 2 - 30, 0, 60, 50);

            txt(g, "FIRE SENSORS: NORMAL", 20, 25, GREEN, 12);
            txt(g, "DOOR OPENED FIRST", w - 210, 25, YELLOW, 12);
            txt(g, "ALARM 3 MINUTES LATER", 20, h - 15, RED, 12);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) { }

            EscapeExe game = new EscapeExe();
            game.setVisible(true);
        });
    }
}