const parser = require('./geekspec_parser');
const fs = require('fs');

var code = fs.readFileSync('./geekapk_v1b_api.geekspec').toString();

code = code.replace(/#.*/g, '');

console.log(code);

console.log("\n\n\n")

let ast = parser.parse(code);

console.log(ast);

function walkOption(o) {
  return o;
}

function walkOptions(os) {
  if (os == null) return "";
  return "{" + os.map(walkOption).join(",") + "}";
}

function walkArg(a) {
  return `${a.name}${a.required ? '!' : '?'}${walkOptions(a.options)}`;
}

function walkArgs(as) {
  return as.map(walkArg)
}

function walkReturn(r) {
  if (r == null) return "noting";
  if (typeof r != 'object') return r;
  if (Array.isArray(r)) {
    return r.map(e => `${e.type}:${e.name}`)
  }
  return `${r.type} of ${r.of}`;
}


function walkInterface(i) {
  console.log()
  console.log(`${i.method} ${i.url}: \t ${i.name}(${walkArgs(i.args)})`);
  console.log(`  Returning ${walkReturn(i.return)}`)
}


ast.forEach(walkInterface);
