package edu.btree.fs;

import edu.btree.core.BTree;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 사례 B 실행 프로그램.
 * 사용법: java edu.btree.fs.FsIndexApp [명령파일=fs-commands.txt] [출력파일=fs-output.txt] [최소차수=3]
 */
public final class FsIndexApp {
    private FsIndexApp() {
    }

    public static void main(String[] args) {
        Path in = Paths.get(args.length > 0 ? args[0] : "fs-commands.txt");
        Path out = Paths.get(args.length > 1 ? args[1] : "fs-output.txt");
        int t;
        try {
            t = args.length > 2 ? Integer.parseInt(args[2]) : 3;
            if (t < 2 || t > BTree.MAX_MIN_DEGREE) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            System.err.println("[오류] 최소 차수는 2 이상 " + BTree.MAX_MIN_DEGREE + " 이하의 정수여야 합니다.");
            return;
        }
        FsCommandProcessor processor = new FsCommandProcessor(new FileSystemIndex(t));
        int lineNo = 0;
        try (BufferedReader reader = Files.newBufferedReader(in, StandardCharsets.UTF_8);
             BufferedWriter writer = Files.newBufferedWriter(out, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineNo++;
                String result = processor.process(line);
                if (result != null) {
                    writer.write(lineNo + ": " + line.trim() + " => " + result);
                    writer.newLine();
                }
            }
            writer.write(processor.summary());
            writer.newLine();
        } catch (NoSuchFileException e) {
            System.err.println("[오류] 파일을 찾을 수 없습니다: " + e.getFile());
            return;
        } catch (IOException e) {
            System.err.println("[오류] 파일 입출력 실패: " + e.getMessage());
            return;
        }
        System.out.println(processor.summary() + " -> " + out.toAbsolutePath());
    }
}
