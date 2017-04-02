const gulp = require("gulp");
const mocha = require("gulp-mocha");
const shell = require("gulp-shell");
const eslint = require("gulp-eslint");
const babel = require("gulp-babel");

gulp.task("test", () => {
  return gulp.src(["test/**/*.js"], { read: false })
    .pipe(mocha({
      reporter: "spec",
      compilers: "js:babel-register"
    }));
});

gulp.task("build", () => {
  return gulp.src(["src/**/*.js"])
    .pipe(babel())
    .pipe(gulp.dest("build/"));
});

gulp.task("eslint", () => {
  return gulp.src(["**/*.js", "!node_modules/**", "!flow-typed/**", "!build/**", "!decls/**"])
    .pipe(eslint())
    .pipe(eslint.format())
    .pipe(eslint.failAfterError());
});

gulp.task("flow-test", shell.task(["flow"]));

gulp.task("default", [ "eslint", "flow-test", "test" ]);
