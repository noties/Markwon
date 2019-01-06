const fs = require('fs');
const path = require('path');

const PROPERTIES_FILE_NAME = 'gradle.properties';
const PROP_GROUP = 'GROUP';
const PROP_DESCRIPTION = 'POM_DESCRIPTION';
const PROP_ARTIFACT_NAME = 'POM_NAME';
const PROP_ARTIFACT_ID = 'POM_ARTIFACT_ID';

const readProperties = (file) => fs.readFileSync(file, { encoding: 'utf-8' }, 'string')
    .split('\n')
    // filter-out empty lines
    .filter(s => s)
    .map(s => s.split('='))
    .reduce((a, s) => {
        a[s[0]] = s[1];
        return a;
    }, {});

const listDirectories = (folder) => fs.readdirSync(folder)
    .map(name => path.join(folder, name))
    .filter(f => fs.lstatSync(f).isDirectory());

const projectDir = path.resolve(__dirname, '../');

const projectProperties = readProperties(path.join(projectDir, PROPERTIES_FILE_NAME));

const projectGroup = projectProperties[PROP_GROUP]

const artifacts = listDirectories(projectDir)
    .map(dir => path.join(dir, PROPERTIES_FILE_NAME))
    .filter(f => fs.existsSync(f))
    .map(readProperties)
    .map(props => {
        return {
            id: props[PROP_ARTIFACT_ID],
            name: props[PROP_ARTIFACT_NAME],
            group: projectGroup,
            description: props[PROP_DESCRIPTION]
        }
    });

const artifactsFile = path.join(__dirname, '.vuepress', '.artifacts.js');
const artifactsJs = `
// this is a generated file, do not modify. To update it run 'collectArtifacts.js' script
const artifacts = ${JSON.stringify(artifacts)};
export { artifacts };
`

fs.writeFileSync(artifactsFile, artifactsJs);
