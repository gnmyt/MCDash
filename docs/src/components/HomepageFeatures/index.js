import React from 'react';
import clsx from 'clsx';
import styles from './styles.module.css';

const FeatureList = [
    {
        title: 'Overview',
        Image: require('@site/static/img/screenshots/overview.png').default,
        description: (
            <>
                Get a quick overview of what your server statistics are. You can also reload and
                shutdown your server with just <b>one</b> button click.
            </>
        ),
    },
    {
        title: 'Manage players',
        Image: require('@site/static/img/screenshots/players.png').default,
        description: (
            <>
                Manage your currently online players. Get a quick glance of what they are
                up to and kick/ban them from your server.
            </>
        ),
    },
    {
        title: 'Manage the console',
        Image: require('@site/static/img/screenshots/console.png').default,
        description: (
            <>
                You don't have to SSH into your server every time you just want to OP
                someone. Use a cloud console to do everything.
            </>
        ),
    }
];

function Feature({Image, title, description}) {
    return (
        <div className={clsx('col col--4')}>
            <div className="text--center">
                <img src={Image} className="feature-img" alt={title} style={{borderRadius: "0.8rem"}} />
            </div>
            <div className="text--center padding-horiz--md">
                <h3>{title}</h3>
                <p>{description}</p>
            </div>
        </div>
    );
}

export default function HomepageFeatures() {
    return (
        <section className={styles.features}>
            <div className="container">
                <div className="row">
                    {FeatureList.map((props, idx) => (
                        <Feature key={idx} {...props} />
                    ))}
                </div>
            </div>
        </section>
    );
}
