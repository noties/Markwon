module.exports = {
    base: '/Markwon/',
    title: 'Markwon',
    description: 'Android markdown library based on commonmark specification that renders markdown as system-native Spannables (no WebView)',
    head: [
        ['link', { rel: 'apple-touch-icon', sizes: '180x180', href: '/apple-touch-icon.png?v=1' }],
        ['link', { rel: 'icon', type: 'image/png', sizes: '16x16', href: '/favicon-16x16.png?v=1' }],
        ['link', { rel: 'icon', href: '/favicon.ico?v=1' }],
        ['link', { rel: 'icon', type: 'image/png', sizes: '32x32', href: '/favicon-32x32.png?v=1' }],
        ['link', { rel: 'manifest', href: '/manifest.json?v=1' }],
        ['meta', { name: 'keywords', content: 'android,markdown,library,spannable,markwon,commonmark' }]
    ],
    themeConfig: {
        nav: [
            { text: 'Install', link: '/docs/install.md' },
            { text: 'Changelog', link: '/CHANGELOG.md' },
            {
                text: 'API Version',
                items: [
                    { text: 'Current (2.x.x)', link: '/' },
                    { text: 'BETA (3.x.x)', link: '/docs/v3/' }
                ]
            },
            { text: 'Sandbox', link: '/sandbox.md' },
            { text: 'Github', link: 'https://github.com/noties/Markwon' }
        ],
        sidebar: {
            '/docs/v3/': [
                '',
                {
                    title: 'Core',
                    collapsable: false,
                    children: [
                        '/docs/v3/core/getting-started.md',
                        '/docs/v3/core/plugins.md',
                        '/docs/v3/core/theme.md',
                        '/docs/v3/core/images.md',
                        '/docs/v3/core/configuration.md',
                        '/docs/v3/core/visitor.md',
                        '/docs/v3/core/spans-factory.md',
                        '/docs/v3/core/html-renderer.md',
                        '/docs/v3/core/core-plugin.md',
                        '/docs/v3/core/movement-method-plugin.md',
                        '/docs/v3/core/render-props.md'
                    ]
                },
                '/docs/v3/ext-latex/',
                '/docs/v3/ext-strikethrough/',
                '/docs/v3/ext-tables/',
                '/docs/v3/ext-tasklist/',
                {
                    title: 'HTML',
                    collapsable: false,
                    children: [
                        '/docs/v3/html/',
                        '/docs/v3/html/custom-tag-handler.md'
                    ]
                },
                '/docs/v3/image/gif.md',
                '/docs/v3/image/okhttp.md',
                '/docs/v3/image/svg.md',
                '/docs/v3/recycler/',
                '/docs/v3/recycler-table/',
                '/docs/v3/syntax-highlight/',
                '/docs/v3/migration-2-3.md'
            ],
            '/': [
                '',
                '/docs/getting-started.md',
                '/docs/configure.md',
                '/docs/theme.md',
                '/docs/factory.md',
                '/docs/image-loader.md',
                '/docs/syntax-highlight.md',
                '/docs/html.md',
                '/docs/view.md'
            ]
        },
        sidebarDepth: 2,
        lastUpdated: true
    },
    markdown: {
        config: md => {
            md.use(require('markdown-it-task-lists'));
        }
    }
}