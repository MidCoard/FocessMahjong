package top.focess.mahjong.terminal.command;

import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;
import top.focess.command.*;

import java.util.List;

public class CommandLine {

    public static final CommandSender DEFAULT_COMMAND_SENDER = new CommandSender(CommandPermission.OWNER) {
    };

    public static final IOHandler DEFAULT_IO_HANDLER = new IOHandler() {
        @Override
        public void output(final String output) {
            System.out.println(output);
        }
    };

    public static CommandResult execute(final String command) {
        final List<String> args = CommandLine.splitCommand(command);
        if (0 == args.size())
            return CommandResult.NONE;
        final String commandName = args.get(0);
        args.remove(0);
        return CommandLine.execute(commandName, args.toArray(new String[0]));
    }

    public static CommandResult execute(final String command, final String[] args) {
        for (final Command com : Command.getCommands())
            if (com.getName().equalsIgnoreCase(command) || com.getAliases().stream().anyMatch(i -> i.equalsIgnoreCase(command)))
                try {
                    return com.execute(CommandLine.DEFAULT_COMMAND_SENDER, args, CommandLine.DEFAULT_IO_HANDLER);
                } catch (final Exception e) {
                    e.printStackTrace();
                }
        return CommandResult.NONE;
    }


    /**
     * Split the command into arguments
     *
     * @param command the command needed to be split
     * @return the split arguments
     */
    @NotNull
    public static List<String> splitCommand(@NotNull final String command) {
        final List<String> args = Lists.newArrayList();
        final StringBuilder stringBuilder = new StringBuilder();
        boolean stack = false;
        boolean ignore = false;
        Character last = null;
        for (final char c : command.toCharArray()) {
            if (ignore) {
                ignore = false;
                switch (c) {
                    case 'a':
                        stringBuilder.append((char) 7);
                        break;
                    case 'b':
                        stringBuilder.append((char) 8);
                        break;
                    case 'f':
                        stringBuilder.append((char) 12);
                        break;
                    case 'n':
                        stringBuilder.append((char) 10);
                        break;
                    case 'r':
                        stringBuilder.append((char) 13);
                        break;
                    case 't':
                        stringBuilder.append((char) 9);
                        break;
                    case 'v':
                        stringBuilder.append((char) 11);
                        break;
                    case '0':
                        stringBuilder.append((char) 0);
                        break;
                    default:
                        stringBuilder.append(c);
                        break;
                }
            } else if ('\\' == c)
                ignore = true;
            else if (' ' == c) {
                if (!stack) {
                    if (0 < stringBuilder.length()) {
                        args.add(stringBuilder.toString());
                        stringBuilder.delete(0, stringBuilder.length());
                    }
                } else
                    stringBuilder.append(' ');
            } else if ('"' == c)
                stack = !stack;
            else if ('@' == c && !stack && null != last && ' ' == last) {
                stringBuilder.append('"');
                stringBuilder.append('@');
            } else stringBuilder.append(c);
            last = c;
        }
        if (0 != stringBuilder.length())
            args.add(stringBuilder.toString());
        return args;
    }
}
