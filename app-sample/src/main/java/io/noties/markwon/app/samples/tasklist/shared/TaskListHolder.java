package io.noties.markwon.app.samples.tasklist.shared;

public abstract class TaskListHolder {

  public static final String MD = "" +
    "- [ ] Not done here!\n" +
    "- [x] and done\n" +
    "- [X] and again!\n" +
    "* [ ] **and** syntax _included_ `code`\n" +
    "- [ ] [link](#)\n" +
    "- [ ] [a check box](https://examp.le)\n" +
    "- [x] [test]()\n" +
    "- [List](https://examp.le) 3";

  private TaskListHolder() {
  }
}
