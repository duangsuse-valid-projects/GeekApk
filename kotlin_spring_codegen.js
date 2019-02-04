const parser = require('./geekspec_parser');
const fs = require('fs');

var code = fs.readFileSync('./geekapk_v1b_api.geekspec').toString();

code = code.replace(/#.*/g, '');

console.log(code);

console.log("\n\n\n")

let ast = parser.parse(code);

console.log(JSON.stringify(ast, null, 2));

function walkOption(o) {
  return o;
}

function walkOptions(os) {
  if (os == null) return "";
  return "/* {" + os.map(walkOption).join(",") + "} */";
}

function walkArg(a) {
  function translateArgLocation(l, n) {
    if (l == 'body')
      return `@RequestBody`;

    if (l == 'path')
      return `@PathVariable("${n}")`;

    return l;
  }

  let match = (a.name.match(/^(?<name_>[a-z]+):(?<type_>[A-Z][a-zA-Z]+)/));

  if (match) {
    let {groups: {name_, type_}} = match
    if (typeof name_ != 'undefined')
      return `@RequestParam("${name_}") ${name_}: ${type_}`;
  }

  let match_ = a.name.match(/^(?<name>[a-z]+)\-(?<location>[a-z]+):(?<type>[A-Z][a-zA-Z]+)/);

  if (match_) {
    let {groups: {name, location, type}} = match_
    if (typeof name != 'undefined')
      return `${translateArgLocation(location, name)} ${name}: ${type}`;
  }

  if (a.required)
    return `@RequestParam("${a.name}") ${a.name}: ${a.type} ${walkOptions(a.options)}`;
  else return `@RequestParam(name = "${a.name.split(':')[0]}", required = false) ${a.name}? ${walkOptions(a.options)}`;
}

function walkArgs(as) {
  return as.map(walkArg)
    .join(', ')
}

function translatePlainTypeName(name) {
  const table = {
    string: 'String',
    number: 'Int',
    boolean: 'Boolean',
    datetime: 'Date',
    plain: 'String'
  };
  let result = table[name];
  if (typeof result == 'undefined')
    return name;
  else return result;
}

function translateReturnType(type, name) {
  if (type == 'array')
    return `List<${name}>`;
  if (type == 'object') {
    if (name.match(/^[A-Z][A-Za-z0-9_].*/))
      return name;
    else return `Map<String, ${translatePlainTypeName(name)}>`;
  }
}

function translateDictReturnType(name, type) {
  return `/* ${name}: ${type} */`;
}

function walkReturn(r) {
  if (r == null) return "Unit";
  if (typeof r != 'object') return translatePlainTypeName(r);
  if (Array.isArray(r)) {
    return "Map<String, String> " + r.map(e => translateDictReturnType(e.name, e.type)).join('')
  }
  return translateReturnType(r.type, r.of);
}

function translatePathBindingAnnotations(verb, url) {
  let perfix = verb[0].toUpperCase() + verb.substring(1, verb.length).toLowerCase()
  return `@${perfix}Mapping("${url}")`
}


function walkInterface(i) {
  console.log()
  console.log(`${translatePathBindingAnnotations(i.method, i.url)}\n@ResponseBody\nfun ${i.name}(${walkArgs(i.args)}): ${walkReturn(i.return)} {\n  TODO()\n}\n`);
}


ast.forEach(walkInterface);
