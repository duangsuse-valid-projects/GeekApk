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
  return "/* Maybe " + os.map(walkOption).join(" or ") + " */";
}

function walkArg(a, url) {
  function translateArgLocation(l, n, optional = false) {
    if (l == 'body') {
      if (optional) console.log("// WARNING: Body parameters should not be optional, ignoring");
      return `@RequestBody`;
    }

    if (l == 'path') {
      if (typeof url != 'undefined' && !url.toString().includes(`{${n}}`))
        console.log(`// WARNING: Path parameter ${n} not included in url ${url}`)
      if (optional) return `@PathVariable(name = "${n}", required = false)`;
      else return `@PathVariable("${n}")`;
    }

    return l;
  }

  let match = (a.name.match(/^(?<name_>[a-z]+):(?<type_>[A-Z][a-zA-Z]+)/));

  if (match) {
    let {groups: {name_, type_}} = match
    if (typeof name_ != 'undefined') {
      if (!a.required)
        return `@RequestParam(name = "${name_}", required = false) ${name_}: ${type_}?${walkOptions(a.options)}`;
      else return `@RequestParam("${name_}") ${name_}: ${type_}${walkOptions(a.options)}`;
    }
  }

  let match_ = a.name.match(/^(?<name>[a-z]+)\-(?<location>[a-z]+):(?<type>[A-Z][a-zA-Z]+)/);

  if (match_) {
    let {groups: {name, location, type}} = match_
    if (typeof name != 'undefined') {
      if (a.required)
        return `${translateArgLocation(location, name)} ${name}: ${type}${walkOptions(a.options)}`;
      else return `${translateArgLocation(location, name, true)} ${name}: ${type}?${walkOptions(a.options)}`;
    }
  }

  if (a.required)
    return `@RequestParam("${a.name.split(':')[0]}}") ${a.name}${walkOptions(a.options)}`;
  else return `@RequestParam(name = "${a.name.split(':')[0]}", required = false) ${a.name}?${walkOptions(a.options)}`;
}

function walkArgs(as, u) {
  return as.map(a => walkArg(a, u))
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

function selectPossibleType(dict) {
  let d = new Map();
  let set = new Set();

  dict.forEach(function (e) {
    //console.log(e);
    for (var name of Object.getOwnPropertyNames(e)) {
      d[name] = e[name];
      //console.log(`// ${name}: ${e[name]}`);
    }
  });

  for (var i in d) {
     if (i == 'type') set.add(d[i]);
  }

  if (set.size == 1)
    return translatePlainTypeName(set.values().next().value);
  else console.log(`// Failed to select a possible type for ${JSON.stringify(d)}, expecting single typeId but found ${JSON.stringify(set)}`);

  return "Any?";
}

function walkReturn(r) {
  if (r == null) return "Unit";
  if (typeof r != 'object') return translatePlainTypeName(r);
  if (Array.isArray(r)) {
    return `Map<String, ${selectPossibleType(r)}> ` + r.map(e => translateDictReturnType(e.name, e.type)).join('')
  }
  return translateReturnType(r.type, r.of);
}

function translatePathBindingAnnotations(verb, url) {
  let prefix = verb[0].toUpperCase() + verb.substring(1, verb.length).toLowerCase()
  return `@${prefix}Mapping("${url}")`
}


function walkInterface(i) {
  console.log()
  var rb = ""
  if (i.return) { rb += "@ResponseBody\n" }

  console.log(`${translatePathBindingAnnotations(i.method, i.url)}\n${rb}fun ${i.name}(${walkArgs(i.args, i.url)}): ${walkReturn(i.return)} {\n  TODO()\n}\n`);
}


ast.forEach(walkInterface);
